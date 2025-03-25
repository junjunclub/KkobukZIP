package com.turtlecoin.auctionservice.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis.key-prefix")
@Getter
public class RedisPrefixProperties {
    private String auctionEnd;
    private String bid;
}
