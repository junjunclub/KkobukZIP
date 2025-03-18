package com.turtlecoin.auctionservice.domain.auction.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.turtlecoin.auctionservice.domain.auction.dto.*;
import com.turtlecoin.auctionservice.domain.auction.entity.*;
import com.turtlecoin.auctionservice.domain.auction.exception.*;
import com.turtlecoin.auctionservice.domain.auction.facade.RedissonLockFacade;
import com.turtlecoin.auctionservice.domain.auction.repository.AuctionRepository;
import com.turtlecoin.auctionservice.domain.s3.exception.S3ExceptionMessage;
import com.turtlecoin.auctionservice.domain.s3.exception.S3UploadFailedException;
import com.turtlecoin.auctionservice.domain.s3.service.ImageUploadService;
import com.turtlecoin.auctionservice.feign.dto.TurtleFilteredResponseDTO;
import com.turtlecoin.auctionservice.feign.dto.TurtleResponseDTO;
import com.turtlecoin.auctionservice.domain.turtle.entity.Gender;
import com.turtlecoin.auctionservice.feign.MainClient;
import com.turtlecoin.auctionservice.feign.dto.UserResponseDTO;
import com.turtlecoin.auctionservice.feign.service.UserService;
import com.turtlecoin.auctionservice.global.exception.*;
import com.turtlecoin.auctionservice.global.response.ResponseVO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final AuctionRepository auctionRepository;
    private final ImageUploadService imageUploadService;  // ImageUploadService도 주입합니다.
    private final MainClient mainClient;
    private final JPAQueryFactory queryFactory;
    private final RedissonLockFacade redissonLockFacade;
    private final SchedulingService schedulingService;
    private final BidService bidService;
    private final SseService sseService;
    private static final String AUCTION_END_KEY_PREFIX = "auction_end_";
    private static final String AUCTION_BID_KEY = "auction_bid_";
    private final UserService userService;

    // 경매 등록
    @Transactional
    public void registerAuction(RegisterAuctionDTO dto, List<MultipartFile> images) {
        // 이미지가 없으면 예외 던지기
        if (images == null || images.isEmpty()) {
            throw new PhotoNotUploadedException();
        }

        validateUserOwnsTurtle(dto.getUserId(), dto.getTurtleId());
        validateTurtleInfo(dto.getTurtleId());
        validateDate(dto.getStartTime());


        List<AuctionPhoto> uploadedPhotos = new ArrayList<>();
//        uploadedPhotos = uploadImages(images, null);  // 경매와 아직 연결되지 않은 상태에서 업로드
        log.info("이미지 업로드 완료");

        // 경매 저장
        Auction auction = auctionRepository.save(dto.toEntity());

        // 동적 스케줄링 수행
        Consumer<Long> startAuction = bidService::startAuction;
        schedulingService.scheduleTask(auction.getId(), startAuction, auction.getStartTime());

        // 이미지 업로드 처리
        uploadedPhotos = uploadImages(images, auction);  // 이미지 업로드
        auction.getAuctionPhotos().addAll(uploadedPhotos);  // 업로드된 이미지 경매와 연결
    }

    // 이미지 업로드 처리 메서드
    private List<AuctionPhoto> uploadImages(List<MultipartFile> images, Auction auction) {
        List<AuctionPhoto> photos = new ArrayList<>();
        try {
            for (MultipartFile image : images) {
                String imagePath = imageUploadService.upload(image, "auctionImages");
                photos.add(AuctionPhoto.builder().imageAddress(imagePath).auction(auction).build());
            }
            return photos;
        } catch (IOException e) {
            throw new S3UploadFailedException();
        }
    }

    // 사용자가 소유한 거북이인지 검증 메서드
    private void validateUserOwnsTurtle(Long userId, Long turtleId) {
        log.info("Main-service에서 조회");
        List<TurtleResponseDTO> userTurtles = getTurtlesByUserId(userId);

        if (userTurtles.isEmpty()) {
            log.error("거북이 정보 조회 불가");
            throw new TurtleNotFoundException();
        }
        log.info("거북이 확인 완료");
        boolean isUserTurtle = userTurtles.stream().anyMatch(turtle -> turtle.getId().equals(turtleId));
        if (!isUserTurtle) {
            throw new TurtleNotFoundException();
        }
        log.info("거북이 일치여부 확인 완료");
    }

    private List<TurtleResponseDTO> getTurtlesByUserId(Long userId) {
        return mainClient.getTurtlesByUserId(userId);
    }

    private void validateDate(LocalDateTime startTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new AuctionTimeNotValidException();
        }
    }

//    private void validateTurtleNotAlreadyRegistered(Long turtleId) {
//        if (auctionRepository.countInProgressAuctionByTurtleId(AuctionProgress.BEFORE_AUCTION, AuctionProgress.DURING_AUCTION, turtleId) > 0) {
//            throw new TurtleAlreadyRegisteredException();
//        }
//    }

    private void validateTurtleInfo(Long turtleId) {
        if (auctionRepository.existsByTurtleId(turtleId)) {
            throw new TurtleAlreadyRegisteredException();
        }
    }

    // 거북이 정보를 가져와서 RegisterAuctionDTO에 설정하는 메서드
    private RegisterAuctionDTO updateAuctionWithTurtleInfo(RegisterAuctionDTO registerAuctionDTO) {
        TurtleFilteredResponseDTO turtleInfo = mainClient.getTurtle(registerAuctionDTO.getTurtleId());

        return RegisterAuctionDTO.builder()
                .turtleId(registerAuctionDTO.getTurtleId())
                .userId(registerAuctionDTO.getUserId())
                .startTime(registerAuctionDTO.getStartTime())
                .minBid(registerAuctionDTO.getMinBid())
                .content(registerAuctionDTO.getContent())
                .title(registerAuctionDTO.getTitle())
                .weight(turtleInfo.getWeight())  // 거북이 무게 설정
                .gender(turtleInfo.getGender())  // 거북이 성별 설정
                .build();
    }

    // 경매 저장 처리 메서드
    private Auction saveAuction(RegisterAuctionDTO registerAuctionDTO) {
        Auction auction = registerAuctionDTO.toEntity();
        log.info("auction: {}", auction);
        log.info("빌더를 이용해서 저장 성공");
        return auctionRepository.save(auction);
    }

    // 업로드된 이미지 삭제 메서드
    public void deleteUploadedImages(List<AuctionPhoto> auctionPhotos) {
        for (AuctionPhoto photo : auctionPhotos) {
            imageUploadService.deleteS3(photo.getImageAddress());
        }
    }

    // 경매 ID로 경매 조회
    public ResponseEntity<?> getAuctionById(Long auctionId) {
        try {
            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(AuctionNotFoundException::new);

            TurtleFilteredResponseDTO turtle = mainClient.getTurtle(auction.getTurtleId());

            if (turtle == null) {
                log.warn("거북이 정보를 찾을 수 없습니다: turtleId={}", auction.getTurtleId());
                throw new TurtleNotFoundException();
            }
            log.info("TurtleID: {}", turtle.getId());
            UserResponseDTO user = mainClient.getUserById(auction.getUserId());
            if (turtle == null) {
                log.warn("사용자 정보를 찾을 수 없습니다: UserId={}", auction.getUserId());
                throw new UserNotFoundException();
            }
            log.info("UserID: {}", user.getUserId());

            String key = AUCTION_END_KEY_PREFIX + auction;
            // null값일 때 어떻게 하지?
            Long remainingTime = redisTemplate.getExpire(AUCTION_END_KEY_PREFIX + auctionId, TimeUnit.MILLISECONDS);

//            // 종료됐거나, 시작하지 않았을 때
//            if (remainingTime == -2) {
//                if (auction.getEndTime().isAfter(LocalDateTime.now())) {
//                    remainingTime = 0L;
//                } else {
//                    // 아직 시작 안한 경매
//                    remainingTime = 0L;
//                }
//            }

            Object bidAmountObj = redisTemplate.opsForHash().get(key, "bidAmount");
            String nickname;
            Double nowBid;
            if (bidAmountObj == null) {
                nowBid = auction.getMinBid();
                log.info("redis에 입찰 가격이 없을 때");
                nickname = null;
            } else {
                nowBid = Double.parseDouble(bidAmountObj.toString());  // Object를 Double로 변환
                Long bidUserId = (long) redisTemplate.opsForHash().get(key, "userId");
                nickname = userService.getUserNicknameById(bidUserId);
                log.info("redis에 입찰 가격이 있을 때");
            }
            log.info("RemainingTime : {}", remainingTime);
            AuctionResponseDTO data = AuctionResponseDTO.from(auction, turtle, user, remainingTime, nowBid, nickname);
            return new ResponseEntity<>(ResponseVO.success("경매가 정상적으로 조회되었습니다.", "auction", data), HttpStatus.OK);
        } catch (AuctionNotFoundException e) {
            return new ResponseEntity<>(ResponseVO.failure("400", e.getMessage()), HttpStatus.BAD_REQUEST);

        } catch (FeignException e) {
            return new ResponseEntity<>(ResponseVO.failure("503", "Main-Service가 응답하지 않습니다." + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseVO.failure("500", "경매 조회 과정 중에 서버 에러가 발생하였습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<AuctionListResponseDto> getMyAuctions(Long userId) {
        // Auction 엔티티 목록 가져오기
        List<Auction> auctions = auctionRepository.findAllByUser(userId);

        // Turtle 정보와 User 정보는 각 Auction과 관련된 데이터를 적절히 조회해서 전달해야 합니다.
        return auctions.stream()
                .map(auction -> {
                    // 첫 번째 이미지 주소 추출
                    String firstImageUrl = auction.getFirstImageUrl();

                    // AuctionResultDTO로 변환
                    return AuctionListResponseDto.builder()

                            .title(auction.getTitle())
                            .content(auction.getContent())
                            .weight(auction.getWeight())
                            .turtleId(auction.getTurtleId())
                            .id(auction.getId())
                            .sellerAddress(auction.getSellerAddress())
                            .auctionFlag(true)
                            .progress(auction.getAuctionProgress())
                            .buyerId(auction.getBuyerId())
                            .sellerId(auction.getUserId())
                            .images(firstImageUrl)
                            .tags(auction.getAuctionTags().stream()
                                    .map(AuctionTag::getTag)
                                    .collect(Collectors.toList())) // 태그 리스트
                            .build();
                })
                .toList(); // 리스트로 수집
    }


    // 경매 필터링 후 조회
    public ResponseEntity<?> getFilteredAuctions(Gender gender, Double minSize, Double maxSize, Double minPrice, Double maxPrice, AuctionProgress progress, int page) {
        try {
            QAuction auction = QAuction.auction;

            BooleanBuilder whereClause = new BooleanBuilder();

            // 가격 필터 (minPrice ~ maxPrice)
            if (minPrice != null) {
                if (maxPrice != null) {
                    whereClause.and(auction.minBid.between(minPrice, maxPrice));
                } else {
                    whereClause.and(auction.minBid.goe(minPrice));
                }
            } else if (maxPrice != null) {
                whereClause.and(auction.minBid.loe(maxPrice));
            }

            // 경매 진행 상태 필터
            if (progress != null) {
                whereClause.and(auction.auctionProgress.eq(progress));
            }

            // main-service에서 필터링 엔드포인트 열어둘 것
            // 무게로 거북이 필터링

            List<TurtleFilteredResponseDTO> filteredTurtles = mainClient.getFilteredTurtles(gender, minSize, maxSize);

            // filteredTurtles 리스트를 Map으로 변환 (turtleId를 키로 사용)
            Map<Long, TurtleFilteredResponseDTO> turtleMap = filteredTurtles.stream()
                    .collect(Collectors.toMap(TurtleFilteredResponseDTO::getId, turtle -> turtle));

            long totalAuctions = queryFactory.selectFrom(auction)
                    .where(whereClause.and(auction.turtleId.in(turtleMap.keySet())))
                    .fetch()
                    .size();

            List<Auction> auctions = queryFactory.selectFrom(auction)
                    .where(whereClause.and(auction.turtleId.in(turtleMap.keySet())))
                    .offset(page * 20L)
                    .limit(20)
                    .fetch();

            // DetailAuctionResponseDTO 리스트 생성
            List<DetailAuctionResponseDTO> dtos = auctions.stream()
                    .map(a -> {
                        UserResponseDTO userInfo = mainClient.getUserById(a.getUserId());
                        TurtleFilteredResponseDTO turtleInfo = turtleMap.get(a.getTurtleId());
                        return DetailAuctionResponseDTO.builder()
                                .auctionId(a.getId())
                                .sellerId(a.getUserId())
                                .sellerName(userInfo.getName())
                                .turtleId(a.getTurtleId())
                                .scientificName("임시 거북이 학명!")
                                .title(a.getTitle())
                                .price(a.getNowBid())
                                .weight(a.getWeight())
                                .content(a.getContent())
                                .sellerImageUrl(userInfo.getProfileImage())
                                .sellerAddress(a.getSellerAddress())
                                .buyerId(a.getBuyerId())
                                .progress(a.getAuctionProgress().toString())
                                .auctionTag(a.getAuctionTags().stream().map(AuctionTag::getTag).collect(Collectors.toList()))
                                .auctionImage(a.getAuctionPhotos().stream().map(AuctionPhoto::getImageUrl).collect(Collectors.toList()))
                                .build();
                    })
                    .toList();

            int totalPages = (int) Math.ceil((double) totalAuctions / 20);

            Map<String, Object> data = new HashMap<>();
            data.put("auctions", dtos);
            data.put("total_pages", totalPages);
            log.info("dtos : {}", dtos);
            return new ResponseEntity<>(ResponseVO.success("경매가 성공적으로 조회 되었습니다.", "data", data), HttpStatus.OK);
        } catch (NumberFormatException e) {
            // 숫자 형식이 잘못된 경우 예외 처리
            return new ResponseEntity<>(ResponseVO.failure("400", "잘못된 형식의 입력값이 있습니다."), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // 기타 잘못된 인자 처리
            return new ResponseEntity<>(ResponseVO.failure("400", "잘못된 파라미터입니다."), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외 처리 (서버 오류)
            e.printStackTrace();  // 로그 출력
            return new ResponseEntity<>(ResponseVO.failure("500", "서버 에러가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    // 거북이 정보를 받아와서 경매정보를 DTO로 변환
//    // 수정, 테스트 필요
//    public AuctionResponseDTO convertToDTO(Auction auction) {
//        log.info("Turtle ID: {}", auction.getTurtleId());
//        TurtleResponseDTO turtleInfo = mainClient.getTurtle(auction.getTurtleId());
//
//        if (turtleInfo == null) {
//            throw new TurtleNotFoundException("Main-service에서 거북이를 가져올 수 없습니다.");
//        }
//        UserResponseDTO userInfo = mainClient.getUserById(auction.getUserId());
//        if (userInfo == null) {
//            throw new UserNotFoundException("Main-service에서 유저 정보를 가져올 수 없습니다.");
//        }
//
//        log.info("Turtle info retrieved: {}", turtleInfo);
//        log.info("User info retrieved: {}", userInfo);
//        return AuctionResponseDTO.from(auction, turtleInfo, userInfo);
//    }

//    public void processBid(Long auctionId, Long userId, Double newBidAmount) {
//        redissonLockFacade.updateBidWithLock(auctionId, userId, newBidAmount);
//    }

    // 서버 재시작시 스케줄링 다시 등록하기
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void scheduleReload() {
        List<Auction> acutions = auctionRepository.findByAuctionProgress(AuctionProgress.BEFORE_AUCTION);

        for (Auction auction : acutions) {
            if (auction.getStartTime().isAfter(LocalDateTime.now())) {
                Consumer<Long> startAuction = bidService::startAuction;
                schedulingService.scheduleTask(auction.getId(), startAuction, auction.getStartTime());
            }
        }
    }
}