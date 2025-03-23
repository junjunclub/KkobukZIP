package com.turtlecoin.auctionservice.domain.auction.dto;

import com.turtlecoin.auctionservice.domain.auction.entity.AuctionProgress;
import com.turtlecoin.auctionservice.domain.turtle.entity.Gender;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AuctionQueryParamsDto {
    private Gender gender;
    @Size(min = 3, max = 20, message = "사이즈의 입력값이 유효하지 않습니다.")
    private String size;  // 예: "10-20"
    @Size(min = 3, max = 40, message = "금액의 입력값이 유효하지 않습니다.")
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
        log.info("input : {}", input);
        if (input == null || input.isBlank()) {
            return new Double[]{null, null}; // 필수 값이 아니므로 null 처리
        }

        if (!input.matches("^\\d{1,6}-\\d{1,6}$")) {
            throw new IllegalArgumentException("입력값은 '숫자-숫자' 형식이어야 합니다.");
        }

        try {
            String[] parts = input.split("-");
            double min = Double.parseDouble(parts[0]);
            double max = Double.parseDouble(parts[1]);

            if (min > max) {
                throw new IllegalArgumentException("최소값은 최대값보다 작아야 합니다.");
            }

            return new Double[]{min, max};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("입력값이 유효한 숫자가 아닙니다.");
        }
    }

}
