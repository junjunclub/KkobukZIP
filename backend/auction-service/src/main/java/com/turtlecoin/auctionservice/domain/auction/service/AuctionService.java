package com.turtlecoin.auctionservice.domain.auction.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.turtlecoin.auctionservice.domain.auction.dto.*;
import com.turtlecoin.auctionservice.domain.auction.entity.*;
import com.turtlecoin.auctionservice.domain.auction.exception.*;
import com.turtlecoin.auctionservice.domain.auction.repository.AuctionRepository;
import com.turtlecoin.auctionservice.domain.s3.exception.S3UploadFailedException;
import com.turtlecoin.auctionservice.domain.s3.service.S3Service;
import com.turtlecoin.auctionservice.feign.dto.TurtleFilteredResponseDTO;
import com.turtlecoin.auctionservice.feign.dto.TurtleResponseDTO;
import com.turtlecoin.auctionservice.feign.MainClient;
import com.turtlecoin.auctionservice.feign.dto.UserResponseDTO;
import com.turtlecoin.auctionservice.feign.service.UserService;
import com.turtlecoin.auctionservice.global.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisKeyUtil redisKeyUtil;
    private final AuctionRepository auctionRepository;
    private final S3Service s3Service;
    private final MainClient mainClient;
    private final JPAQueryFactory queryFactory;
    private final SchedulingService schedulingService;
    private final BidService bidService;
    private final UserService userService;

    @Transactional
    public void registerAuction(CreateAuctionRequestDto dto, List<MultipartFile> images) {
        imageValidation(images);
        validateTurtleOwnershipByUser(dto.getUserId(), dto.getTurtleId());
        validateTurtleNotRegistered(dto.getTurtleId());

        Auction auction = auctionRepository.save(dto.toEntity());

        // 동적 스케줄링 수행
        Consumer<Long> startAuction = bidService::startAuction;
        schedulingService.scheduleTask(auction.getId(), startAuction, auction.getStartTime());

        // 이미지 업로드 처리
        List<AuctionPhoto> uploadedPhotos = uploadImages(images, auction);
        auction.getAuctionPhotos().addAll(uploadedPhotos);
    }

    public AuctionDetailResponseDto getAuctionById(Long auctionId) {
        Auction auction = getAuction(auctionId);
        TurtleFilteredResponseDTO turtle = getTurtleFilteredResponseDTO(auction);
        UserResponseDTO user = getUserResponseDTO(auction);

        RedisAuctionInfoDto redisInfo = getAuctionInfoFromRedis(auction);

        return AuctionDetailResponseDto.from(
                auction, turtle, user,
                redisInfo.getRemainingTime(),
                redisInfo.getNowBid(),
                redisInfo.getBidNickname());
    }

    public AuctionListResponseDto getMyAuctions(Long userId) {
        List<Auction> auctions = auctionRepository.findAllByUser(userId);
        return AuctionListResponseDto.from(auctions);
    }

    public AuctionFilterResultDto getFilteredAuctions(AuctionQueryParamsDto filter) {
        QAuction auction = QAuction.auction;
        BooleanBuilder whereClause = buildWhereClause(filter);

        List<TurtleFilteredResponseDTO> filteredTurtles = getFilteredTurtles(filter);
        Map<Long, TurtleFilteredResponseDTO> turtleMap = convertToTurtleMap(filteredTurtles);

        List<AuctionProjectionDto> auctions = fetchAuctionProjections(filter, auction, whereClause, turtleMap);

        Set<Long> userIds = auctions.stream()
                .map(AuctionProjectionDto::getUserId)
                .collect(Collectors.toSet());

        Map<Long, UserResponseDTO> userMap = mainClient.getUsersByIds(new ArrayList<>(userIds)).stream()
                .collect(Collectors.toMap(UserResponseDTO::getUserId, u -> u));

        List<DetailAuctionResponseDTO> dtos = auctions.stream()
                .map(proj -> {
                    UserResponseDTO user = userMap.get(proj.getUserId());
                    TurtleFilteredResponseDTO turtle = turtleMap.get(proj.getTurtleId());
                    return DetailAuctionResponseDTO.fromProjection(proj, user, turtle);
                })
                .toList();

        int totalPages = calculateTotalPages(auction, whereClause, turtleMap);

        return AuctionFilterResultDto.from(dtos, totalPages, filter.getPage());
    }

//    private List<Auction> fetchFilteredAuctionsFromQueryDsl(
//            AuctionQueryParamsDto filter, QAuction auction,
//            BooleanBuilder whereClause, Map<Long, TurtleFilteredResponseDTO> turtleMap) {
//
//        List<Long> auctionIds = getPagedAuctionIds(filter, whereClause, turtleMap);
//
//        if (auctionIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return getDetailAuctions(auctionIds);
//    }

    private List<AuctionProjectionDto> fetchAuctionProjections(
            AuctionQueryParamsDto filter,
            QAuction auction,
            BooleanBuilder whereClause,
            Map<Long, TurtleFilteredResponseDTO> turtleMap
    ) {
        return queryFactory
                .select(Projections.constructor(AuctionProjectionDto.class,
                        auction.id,
                        auction.turtleId,
                        auction.userId,
                        auction.title,
                        auction.nowBid,
                        auction.sellerAddress,
                        auction.createDate,
                        auction.auctionProgress.stringValue(),
                        auction.buyerId,
                        auction.weight,
                        auction.content
                ))
                .from(auction)
                .where(whereClause.and(auction.turtleId.in(turtleMap.keySet())))
                .orderBy(auction.createDate.desc())
                .offset(filter.getPage() * 20L)
                .limit(20)
                .fetch();
    }

    private List<Long> getPagedAuctionIds(
            AuctionQueryParamsDto filter,
            BooleanBuilder whereClause,
            Map<Long, TurtleFilteredResponseDTO> turtleMap) {

        QAuction auction = QAuction.auction;

        return queryFactory.select(auction.id)
                .from(auction)
                .where(whereClause.and(auction.turtleId.in(turtleMap.keySet())))
                .orderBy(auction.createDate.desc())
                .offset(filter.getPage() * 20L)
                .limit(20)
                .fetch();
    }

    private List<Auction> getDetailAuctions(List<Long> auctionIds) {
        QAuction auction = QAuction.auction;
        QAuctionTag auctionTag = QAuctionTag.auctionTag;

        return queryFactory.selectFrom(auction)
                .leftJoin(auction.auctionTags, auctionTag).fetchJoin()
                .where(auction.id.in(auctionIds))
                .distinct()
                .fetch();
    }

    private int calculateTotalPages(QAuction auction, BooleanBuilder whereClause, Map<Long, TurtleFilteredResponseDTO> turtleMap) {
        Long totalCount = queryFactory
                .select(auction.count())
                .from(auction)
                .where(whereClause.and(auction.turtleId.in(turtleMap.keySet())))
                .fetchOne();

        return (int) Math.ceil((totalCount != null ? totalCount : 0) / 20.0);
    }

    private BooleanBuilder buildWhereClause(AuctionQueryParamsDto filter) {
        QAuction auction = QAuction.auction;
        BooleanBuilder whereClause = new BooleanBuilder();

        addPriceCondition(whereClause, auction, filter.getMinPrice(), filter.getMaxPrice());
        addProgressCondition(whereClause, auction, filter.getProgress());

        return whereClause;
    }

    private List<TurtleResponseDTO> fetchUserTurtles(Long userId) {
        return mainClient.getTurtlesByUserId(userId);
    }

    private void addPriceCondition(BooleanBuilder builder, QAuction auction, Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null) {
            builder.and(auction.minBid.between(minPrice, maxPrice));
        } else if (minPrice != null) {
            builder.and(auction.minBid.goe(minPrice));
        } else if (maxPrice != null) {
            builder.and(auction.minBid.loe(maxPrice));
        }
    }

    private void addProgressCondition(BooleanBuilder builder, QAuction auction, AuctionProgress progress) {
        if (progress != null) {
            builder.and(auction.auctionProgress.eq(progress));
        }
    }

    private Map<Long, TurtleFilteredResponseDTO> convertToTurtleMap(List<TurtleFilteredResponseDTO> turtles) {
        return turtles.stream()
                .collect(Collectors.toMap(TurtleFilteredResponseDTO::getId, t -> t));
    }

    private List<TurtleFilteredResponseDTO> getFilteredTurtles(AuctionQueryParamsDto filter) {
        List<TurtleFilteredResponseDTO> turtles = mainClient.getFilteredTurtles(
                filter.getGender(), filter.getMinSize(), filter.getMaxSize()
        );

        if (turtles == null || turtles.isEmpty()) {
            return Collections.emptyList();
        }
        return turtles;
    }

    private DetailAuctionResponseDTO convertToDetailDTO(
            Auction auction,
            Map<Long, TurtleFilteredResponseDTO> turtleMap,
            Map<Long, UserResponseDTO> userMap) {

        UserResponseDTO userInfo = userMap.get(auction.getUserId());
        TurtleFilteredResponseDTO turtleInfo = turtleMap.get(auction.getTurtleId());

        if (userInfo == null || turtleInfo == null) {
            throw new TurtleNotFoundException();
        }

        return DetailAuctionResponseDTO.from(
                auction,
                userInfo,
                turtleInfo,
                auction.getAuctionTags().stream().map(AuctionTag::getTag).toList(),
                auction.getAuctionPhotos().stream().map(AuctionPhoto::getImageUrl).toList()
        );
    }

    private Auction getAuction(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(AuctionNotFoundException::new);
    }

    private static void imageValidation(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new PhotoNotUploadedException();
        }
    }

    private void validateTurtleOwnershipByUser(Long userId, Long turtleId) {
        boolean ownsTurtle = fetchUserTurtles(userId).stream()
                .anyMatch(turtle -> turtle.getId().equals(turtleId));

        if (!ownsTurtle) {
            throw new TurtleNotOwnedException();
        }
    }

    private void validateTurtleNotRegistered(Long turtleId) {
        if (auctionRepository.existsByTurtleId(turtleId)) {
            throw new TurtleAlreadyRegisteredException();
        }
    }

    private UserResponseDTO getUserResponseDTO(Auction auction) {
        return validateNotNull(
                mainClient.getUserById(auction.getUserId()),
                new UserNotFoundException()
        );
    }

    private TurtleFilteredResponseDTO getTurtleFilteredResponseDTO(Auction auction) {
        return validateNotNull(
                mainClient.getTurtle(auction.getTurtleId()),
                new TurtleNotFoundException()
        );
    }

    private <T> T validateNotNull(T obj, RuntimeException exception) {
        if (obj == null) throw exception;
        return obj;
    }

    private List<AuctionPhoto> uploadImages(List<MultipartFile> images, Auction auction) {
        List<AuctionPhoto> photos = new ArrayList<>();
        try {
            for (MultipartFile image : images) {
                String imagePath = s3Service.upload(image, "auctionImages");
                photos.add(AuctionPhoto.builder().imageAddress(imagePath).auction(auction).build());
            }
            return photos;
        } catch (IOException e) {
            deleteUploadedImages(photos);
            throw new S3UploadFailedException();
        }
    }

    private void deleteUploadedImages(List<AuctionPhoto> auctionPhotos) {
        for (AuctionPhoto photo : auctionPhotos) {
            s3Service.deleteS3(photo.getImageAddress());
        }
    }

    private RedisAuctionInfoDto getAuctionInfoFromRedis(Auction auction) {
        String key = redisKeyUtil.auctionEndKey(auction.getId());

        Long remainingTime = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        Object bidAmountObj = redisTemplate.opsForHash().get(key, "bidAmount");

        if (bidAmountObj == null) {
            return new RedisAuctionInfoDto(remainingTime, auction.getMinBid(), null);
        }

        Double nowBid = Double.parseDouble(bidAmountObj.toString());
        Long bidUserId = (Long) redisTemplate.opsForHash().get(key, "userId");
        String nickname = userService.getUserNicknameById(bidUserId);

        return new RedisAuctionInfoDto(remainingTime, nowBid, nickname);
    }

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