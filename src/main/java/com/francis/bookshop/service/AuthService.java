package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;
import com.francis.bookshop.dto.UserLoginDto;

public interface AuthService {
  UserDto register(UserDto userDto);

  UserDto authenticate(UserLoginDto userLoginDto);

  boolean verifyMfaCode(String username, int code);

  void ensureAdmin(String username);

  UserDto findByUsername(String username);
}
