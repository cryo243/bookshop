package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;
import com.francis.bookshop.dto.UserLoginDto;
import com.francis.bookshop.entity.Role;
import com.francis.bookshop.entity.User;
import com.francis.bookshop.enums.UserRole;
import com.francis.bookshop.repository.RoleRepository;
import com.francis.bookshop.repository.UserRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoginAttemptService loginAttemptService;
  private final RoleRepository roleRepository;
  private final MFAService mfaService;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Autowired
  public AuthServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      LoginAttemptService loginAttemptService,
      RoleRepository roleRepository,
      MFAService mfaService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.loginAttemptService = loginAttemptService;
    this.roleRepository = roleRepository;
    this.mfaService = mfaService;
  }

  @Override
  public UserDto register(UserDto userDto) {
    if (userRepository.existsByUsername(userDto.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }
    // Validate role from enum
    UserRole userRole =
        UserRole.isValidRole(userDto.getRole())
            ? UserRole.valueOf(userDto.getRole().toUpperCase())
            : UserRole.USER;

    // Lookup role from roles table
    Role roleEntity =
        roleRepository
            .findByName(userRole)
            .orElseThrow(
                () -> new IllegalArgumentException("Role " + userRole + " not found in DB"));

    User newUser = new User();
    String mfaUri = null;
    LocalDate dob =
        userDto.getDateOfBirth() != null
            ? LocalDate.parse(userDto.getDateOfBirth(), DATE_FORMATTER)
            : null;
    newUser.setUsername(userDto.getUsername());
    newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
    newUser.setName(userDto.getName());
    newUser.setSurname(userDto.getSurname());
    newUser.setRole(roleEntity);
    newUser.setEmail(userDto.getEmail());
    newUser.setDateOfBirth(dob);
    newUser.setAddress(userDto.getAddress());
    newUser.setPhoneNumber(userDto.getPhoneNumber());

    if (userDto.isUsing2FA()) {
      String secret = mfaService.generateSecret(); // generate a random secret
      newUser.setMfaSecret(secret);
      newUser.setMfaEnabled(true);
      mfaUri = mfaService.provisioningUri(userDto);
    }

    User savedUser = userRepository.save(newUser);
    return toDto(savedUser, mfaUri);
  }

  @Override
  public UserDto authenticate(UserLoginDto userLoginDto) {

    if (loginAttemptService.isBlocked(userLoginDto.getUsername())) {
      throw new ResponseStatusException(
          HttpStatus.TOO_MANY_REQUESTS, "Account locked. Too many attempts");
    }
    Optional<User> userOptional = userRepository.findByUsername(userLoginDto.getUsername());

    if (userOptional.isPresent()) {
      User user = userOptional.get();

      if (passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
        return toDto(user, null);
      } else {
        loginAttemptService.recordFailure(userLoginDto.getUsername());
        log.error("failed login attempt username {}", userLoginDto.getUsername());
        throw new IllegalArgumentException("Invalid credentials");
      }
    } else {
      loginAttemptService.recordFailure(userLoginDto.getUsername());
      log.error("login failure username {}", userLoginDto.getUsername());
      throw new IllegalArgumentException("Invalid credentials");
    }
  }

  @Override
  public boolean verifyMfaCode(String username, int code) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    return mfaService.verifyCode(user.getMfaSecret(), code);
  }

  @Override
  public void ensureAdmin(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.getRole().getName() != UserRole.ADMIN) {
      throw new AccessDeniedException("You do not have permission to perform this action");
    }
  }

  @Override
  public UserDto findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .map(user -> toDto(user, null))
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  private UserDto toDto(User user, String uri) {
    return UserDto.builder()
        .surname(user.getSurname())
        .name(user.getName())
        .username(user.getUsername())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .dateOfBirth(
            user.getDateOfBirth() != null ? user.getDateOfBirth().format(DATE_FORMATTER) : null)
        .address(user.getAddress())
        .role(user.getRole().getName().toString())
        .mfaUri(uri)
        .isUsing2FA(user.isMfaEnabled())
        .build();
  }
}
