package com.turtlecoin.auctionservice.domain.auction.dto;

import com.turtlecoin.auctionservice.domain.auction.entity.Auction;
import com.turtlecoin.auctionservice.domain.auction.entity.AuctionProgress;
import com.turtlecoin.auctionservice.domain.auction.entity.AuctionTag;
import com.turtlecoin.auctionservice.domain.turtle.entity.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@NoArgsConstructor
// DTO 수정해야함.
public class RegisterAuctionDTO {
    @NotNull(message = "거북이 ID는 필수입니다.")
    private Long turtleId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "경매 시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    @NotNull(message = "최소 입찰가는 필수입니다.")
    @Min(value = 0, message = "최소 입찰가는 0 이상이어야 합니다.")
    private Double minBid;

    @NotBlank(message = "경매 내용을 입력해야 합니다.")
    private String content;

    @NotBlank(message = "경매 제목을 입력해야 합니다.")
    private String title;

    @Min(value = 1, message = "무게는 1 이상이어야 합니다.")
    private int weight; // 거북이 무게

    @NotBlank(message = "판매자 주소는 필수입니다.")
    private String sellerAddress;

    @NotNull(message = "거북이 성별은 필수입니다.")
    private Gender gender;

    private List<String> auctionTags;

    @Builder
    public RegisterAuctionDTO(Long turtleId, Long userId, LocalDateTime startTime, Double minBid,
                              String content, String title, int weight, String sellerAddress, Gender gender,
                              List<String> auctionTags) {
        this.turtleId = turtleId;
        this.userId = userId;
        this.startTime = startTime;
        this.minBid = minBid;
        this.content = content;
        this.title = title;
        this.weight = weight;
        this.sellerAddress = sellerAddress;
        this.gender = gender;
        this.auctionTags = auctionTags;
    }

    public Auction toEntity() {
        Auction auction = Auction.builder()
                .turtleId(turtleId)
                .userId(userId)
                .startTime(startTime)
                .minBid(minBid)
                .content(content)
                .title(title)
                .nowBid(minBid)
                .weight(weight)
                .sellerAddress(sellerAddress)
                .auctionProgress(AuctionProgress.BEFORE_AUCTION)
                .endTime(startTime.plusSeconds(30))
                .build();

        // 태그가 있는 경우 AuctionTag 생성 및 Auction과 연관 설정
        if (auctionTags != null && !auctionTags.isEmpty()) {
            List<AuctionTag> tagEntities = auctionTags.stream()
                    .map(tag -> new AuctionTag(auction, tag))
                    .collect(Collectors.toList());

            auction.addAuctionTags(tagEntities);
        }

        return auction;
    }
}
