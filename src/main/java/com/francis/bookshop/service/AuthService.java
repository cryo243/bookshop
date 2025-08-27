package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;
import com.francis.bookshop.dto.UserLoginDto;
import com.francis.bookshop.enums.UserRole;

public interface AuthService {
     UserDto register(UserDto userDto);
     UserDto authenticate(UserLoginDto userLoginDto);
     boolean setUserRole(String username, UserRole role);

}
