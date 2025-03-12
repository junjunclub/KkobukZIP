package com.turtlecoin.auctionservice.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionTag is a Querydsl query type for AuctionTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionTag extends EntityPathBase<AuctionTag> {

    private static final long serialVersionUID = -1974573163L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionTag auctionTag = new QAuctionTag("auctionTag");

    public final com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity _super = new com.turtlecoin.auctionservice.domain.global.entity.QBaseEntity(this);

    public final QAuction auction;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath tag = createString("tag");

    public QAuctionTag(String variable) {
        this(AuctionTag.class, forVariable(variable), INITS);
    }

    public QAuctionTag(Path<? extends AuctionTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionTag(PathMetadata metadata, PathInits inits) {
        this(AuctionTag.class, metadata, inits);
    }

    public QAuctionTag(Class<? extends AuctionTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction")) : null;
    }

}

