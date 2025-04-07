package com.turtlecoin.auctionservice.domain.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class AuctionProjectionDto {
    private Long auctionId;
    private Long turtleId;
    private Long userId;
    private String title;
    private Double nowBid;
    private String sellerAddress;
    private LocalDateTime createDate;
    private String progress;
    private Long buyerId;
    private int weight;
    private String content;
}
