package com.turtlecoin.auctionservice.domain.auction.repository;

import com.turtlecoin.auctionservice.domain.auction.dto.AuctionTagProjectionDto;
import com.turtlecoin.auctionservice.domain.auction.entity.AuctionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionTagRepository extends JpaRepository<AuctionTag, Long> {
    @Query("SELECT new com.turtlecoin.auctionservice.domain.auction.dto.AuctionTagProjectionDto(t.auction.id, t.tag) " +
            "FROM AuctionTag t WHERE t.auction.id IN :auctionIds")
    List<AuctionTagProjectionDto> findTagsByAuctionIds(@Param("auctionIds") List<Long> auctionIds);

}
