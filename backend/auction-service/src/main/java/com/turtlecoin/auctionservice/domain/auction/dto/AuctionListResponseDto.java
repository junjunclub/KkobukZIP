package com.turtlecoin.auctionservice.domain.auction.dto;

import com.turtlecoin.auctionservice.domain.auction.entity.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionListResponseDto {
    private List<AuctionResponseDto> auctions;
    private int totalCount;

    public static AuctionListResponseDto from (List<Auction> auctions) {
        List<AuctionResponseDto> dtos = auctions.stream()
                .map(AuctionResponseDto::from)
                .toList();

        return AuctionListResponseDto.builder()
                .auctions(dtos)
                .totalCount(dtos.size())
                .build();
    }
}