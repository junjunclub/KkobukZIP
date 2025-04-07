package com.turtlecoin.auctionservice.global.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {
    public static <T> Map<Long, List<String>> groupByAuctionId(
            List<T> projections,
            Function<T, Long> idExtractor,
            Function<T, String> valueExtractor
    ) {
        return projections.stream()
                .collect(Collectors.groupingBy(
                        idExtractor,
                        Collectors.mapping(valueExtractor, Collectors.toList())
                ));
    }
}
