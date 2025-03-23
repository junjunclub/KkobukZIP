package com.turtlecoin.auctionservice.feign.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;
    private String nickname;
    private String email;
    private String name;
    private String profileImage;
}
