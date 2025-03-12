package com.turtlecoin.auctionservice.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = 904744389L;

    public static final QAuction auction = new QAuction("auction");

    public final com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity _super = new com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity(this);

    public final ListPath<AuctionPhoto, QAuctionPhoto> auctionPhotos = this.<AuctionPhoto, QAuctionPhoto>createList("auctionPhotos", AuctionPhoto.class, QAuctionPhoto.class, PathInits.DIRECT2);

    public final EnumPath<AuctionProgress> auctionProgress = createEnum("auctionProgress", AuctionProgress.class);

    public final ListPath<AuctionTag, QAuctionTag> auctionTags = this.<AuctionTag, QAuctionTag>createList("auctionTags", AuctionTag.class, QAuctionTag.class, PathInits.DIRECT2);

    public final NumberPath<Long> buyerId = createNumber("buyerId", Long.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Double> minBid = createNumber("minBid", Double.class);

    public final NumberPath<Double> nowBid = createNumber("nowBid", Double.class);

    public final StringPath sellerAddress = createString("sellerAddress");

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> turtleId = createNumber("turtleId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

    public final NumberPath<Double> winningBid = createNumber("winningBid", Double.class);

    public QAuction(String variable) {
        super(Auction.class, forVariable(variable));
    }

    public QAuction(Path<? extends Auction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuction(PathMetadata metadata) {
        super(Auction.class, metadata);
    }

}

