package com.turtlecoin.mainservice.domain.user.dto;

import com.turtlecoin.mainservice.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;
    private String uuid;
    private String nickname;
    private String email;
    private String name;
    private String address;
    private LocalDate birth;
    private String profileImage;

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUuid(),
                user.getNickname(),
                user.getEmail(),
                user.getName(),
                user.getAddress(),
                user.getBirth(),
                user.getProfileImage()
        );
    }
}
