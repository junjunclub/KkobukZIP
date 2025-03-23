package com.turtlecoin.auctionservice.domain.auction.dto;

import lombok.*;


import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionFilterResultDto {
    private List<DetailAuctionResponseDTO> auctions;
    private int totalPages;
    private int page;

    public static AuctionFilterResultDto from(
            List<DetailAuctionResponseDTO> auctions,
            int totalPages,
            int page
    ) {
        return AuctionFilterResultDto.builder()
                .auctions(auctions)
                .totalPages(totalPages)
                .page(page)
                .build();
    }
}