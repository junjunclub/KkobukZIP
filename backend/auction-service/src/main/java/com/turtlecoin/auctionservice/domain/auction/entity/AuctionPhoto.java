package com.turtlecoin.auctionservice.domain.auction.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.turtlecoin.auctionservice.domain.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "auction_photo")
@BatchSize(size = 20)
public class AuctionPhoto extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    @JsonBackReference
    private Auction auction;

    public String getImageUrl() {
        return this.imageAddress;
    }
}
