package com.francis.bookshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserLoginDto {
  @NotEmpty private String username;
  @NotEmpty private String password;
}
