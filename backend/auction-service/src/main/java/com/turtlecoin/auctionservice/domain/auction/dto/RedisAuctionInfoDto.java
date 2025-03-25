package com.turtlecoin.auctionservice.domain.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisAuctionInfoDto {
    private Long remainingTime;
    private Double nowBid;
    private String bidNickname;
}
