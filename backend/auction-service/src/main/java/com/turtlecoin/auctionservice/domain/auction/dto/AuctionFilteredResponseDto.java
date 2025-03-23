package com.turtlecoin.auctionservice.domain.auction.dto;

import lombok.*;


import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionFilteredResponseDto {
    private List<DetailAuctionResponseDTO> auctions;
    private int totalPages;
    private int page;

    public static AuctionFilteredResponseDto from(
            List<DetailAuctionResponseDTO> auctions,
            int totalPages,
            int page
    ) {
        return AuctionFilteredResponseDto.builder()
                .auctions(auctions)
                .totalPages(totalPages)
                .page(page)
                .build();
    }
}