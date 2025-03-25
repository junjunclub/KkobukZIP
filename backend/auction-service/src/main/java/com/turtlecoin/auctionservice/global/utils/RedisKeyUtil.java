package com.turtlecoin.auctionservice.global.utils;

import com.turtlecoin.auctionservice.global.config.RedisPrefixProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyUtil {

    private final RedisPrefixProperties redisPrefixProperties;

    public String auctionEndKey(Long auctionId) {
        return redisPrefixProperties.getAuctionEnd() + auctionId;
    }

    public String auctionBidKey(Long auctionId) {
        return redisPrefixProperties.getBid() + auctionId;
    }
}
