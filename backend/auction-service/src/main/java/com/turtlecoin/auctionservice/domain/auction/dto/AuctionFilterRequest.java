package com.turtlecoin.auctionservice.domain.auction.dto;

import com.turtlecoin.auctionservice.domain.auction.entity.AuctionProgress;
import com.turtlecoin.auctionservice.domain.turtle.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class AuctionFilterRequest {
    private Gender gender;
    private String size;  // 예: "10-20"
    private String price; // 예: "1000-3000"
    private AuctionProgress progress;
    private int page = 0;

    public Double getMinSize() {
        return extractRange(size)[0];
    }

    public Double getMaxSize() {
        return extractRange(size)[1];
    }

    public Double getMinPrice() {
        return extractRange(price)[0];
    }

    public Double getMaxPrice() {
        return extractRange(price)[1];
    }

    private Double[] extractRange(String input) {
        if (input == null || !input.contains("-")) return new Double[]{null, null};
        try {
            String[] parts = input.split("-");
            if (parts.length == 2) {
                return new Double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
        return new Double[]{null, null};
    }
}
