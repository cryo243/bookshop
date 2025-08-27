package com.francis.bookshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserLoginDto {
   private String username;
    private String password;
}
