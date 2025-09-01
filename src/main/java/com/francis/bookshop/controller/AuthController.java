package com.francis.bookshop.controller;

import com.francis.bookshop.dto.MfaVerificationRequest;
import com.francis.bookshop.dto.UserDto;
import com.francis.bookshop.dto.UserLoginDto;
import com.francis.bookshop.dto.UserLoginResponseDto;
import com.francis.bookshop.service.AuthService;
import com.francis.bookshop.service.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
    this.authService = authService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
    UserDto createdUser = authService.register(userDto);
    return ResponseEntity.ok(createdUser);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto) {

    UserDto authenticatedUser = authService.authenticate(userLoginDto);

    if (authenticatedUser.isUsing2FA()) {
      return ResponseEntity.ok(
          UserLoginResponseDto.builder()
              .mfaRequired(true)
              .role(authenticatedUser.getRole())
              .build());
    }
    String token = jwtTokenProvider.generateToken(authenticatedUser);
    UserLoginResponseDto userLoginResponseDto =
        UserLoginResponseDto.builder()
            .role(authenticatedUser.getRole())
            .token(token)
            .mfaRequired(false)
            .build();

    return ResponseEntity.ok(userLoginResponseDto);
  }

  @PostMapping("/verify-mfa")
  public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerificationRequest request) {
    boolean verified = authService.verifyMfaCode(request.getUsername(), request.getCode());
    if (!verified) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid MFA code");
    }

    UserDto user = authService.findByUsername(request.getUsername());
    String token = jwtTokenProvider.generateToken(user);

    return ResponseEntity.ok(
        UserLoginResponseDto.builder()
            .token(token)
            .role(user.getRole())
            .mfaRequired(false)
            .build());
  }
}
