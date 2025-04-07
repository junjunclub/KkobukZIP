package com.turtlecoin.auctionservice.domain.auction.repository;

import com.turtlecoin.auctionservice.domain.auction.dto.AuctionPhotoProjectionDto;
import com.turtlecoin.auctionservice.domain.auction.entity.AuctionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionPhotoRepository extends JpaRepository<AuctionPhoto, Long> {
    @Query("SELECT new com.turtlecoin.auctionservice.domain.auction.dto.AuctionPhotoProjectionDto(p.auction.id, p.imageAddress) " +
            "FROM AuctionPhoto p WHERE p.auction.id IN :auctionIds")
    List<AuctionPhotoProjectionDto> findPhotosByAuctionIds(@Param("auctionIds") List<Long> auctionIds);

}
