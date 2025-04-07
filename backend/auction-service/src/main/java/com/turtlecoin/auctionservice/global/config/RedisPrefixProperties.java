package com.turtlecoin.auctionservice.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis.key-prefix")
@Getter
@Setter
public class RedisPrefixProperties {
    private String auctionEnd;
    private String bid;
}
