package com.francis.bookshop.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserLoginResponseDto {
    private String token;
    private String role;
}
