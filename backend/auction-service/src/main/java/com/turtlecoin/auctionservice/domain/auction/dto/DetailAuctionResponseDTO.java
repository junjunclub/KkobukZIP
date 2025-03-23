package com.turtlecoin.auctionservice.domain.auction.dto;

import com.turtlecoin.auctionservice.domain.auction.entity.Auction;
import com.turtlecoin.auctionservice.feign.dto.TurtleFilteredResponseDTO;
import com.turtlecoin.auctionservice.feign.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailAuctionResponseDTO {

    private Long auctionId;
    private Long sellerId;
    private String sellerName;
    private Long turtleId;
    private String scientificName;
    private Double price;
    private String createDate;
    private String title;
    private String sellerImageUrl;
    private String sellerAddress;
    private Long buyerId;
    private int weight;
    private String content;
    private List<String> auctionTag;
    private List<String> auctionImage;
    private String progress;

    public static DetailAuctionResponseDTO from(
            Auction auction,
            UserResponseDTO user,
            TurtleFilteredResponseDTO turtle,
            List<String> tags,
            List<String> images
    ) {
        return DetailAuctionResponseDTO.builder()
                .auctionId(auction.getId())
                .sellerId(user.getUserId())
                .sellerName(user.getName())
                .turtleId(turtle.getId())
                .scientificName(turtle.getScientificName())
                .price(auction.getNowBid())
                .createDate(formatDate(auction.getCreateDate()))
                .title(auction.getTitle())
                .sellerImageUrl(user.getProfileImage())
                .sellerAddress(auction.getSellerAddress())
                .buyerId(auction.getBuyerId())
                .weight(auction.getWeight())
                .content(auction.getContent())
                .auctionTag(tags)
                .auctionImage(images)
                .progress(auction.getAuctionProgress().name())
                .build();
    }

    private static String formatDate(LocalDateTime dateTime) {
        return dateTime.toString();
    }
}
