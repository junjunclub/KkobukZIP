package com.turtlecoin.auctionservice.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionPhoto is a Querydsl query type for AuctionPhoto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionPhoto extends EntityPathBase<AuctionPhoto> {

    private static final long serialVersionUID = 807261037L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionPhoto auctionPhoto = new QAuctionPhoto("auctionPhoto");

    public final com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity _super = new com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity(this);

    public final QAuction auction;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageAddress = createString("imageAddress");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public QAuctionPhoto(String variable) {
        this(AuctionPhoto.class, forVariable(variable), INITS);
    }

    public QAuctionPhoto(Path<? extends AuctionPhoto> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionPhoto(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionPhoto(PathMetadata metadata, PathInits inits) {
        this(AuctionPhoto.class, metadata, inits);
    }

    public QAuctionPhoto(Class<? extends AuctionPhoto> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction")) : null;
    }

}

