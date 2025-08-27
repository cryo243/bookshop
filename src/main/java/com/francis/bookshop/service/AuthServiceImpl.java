package com.francis.bookshop.service;

import com.francis.bookshop.dto.UserDto;
import com.francis.bookshop.dto.UserLoginDto;
import com.francis.bookshop.entity.User;
import com.francis.bookshop.enums.UserRole;
import com.francis.bookshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserRole
            role =
            (UserRole.isValidRole(userDto.getRole())) ? UserRole.valueOf(userDto.getRole().toUpperCase())
                                                      : UserRole.USER;
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setName(userDto.getName());
        newUser.setSurname(userDto.getSurname());
        newUser.setRole(role);
        newUser.setEmail(userDto.getEmail());
        newUser.setDateOfBirth(userDto.getDateOfBirth());
        newUser.setAddress(userDto.getAddress());
        newUser.setPhoneNumber(userDto.getPhoneNumber());
        User savedUser = userRepository.save(newUser);
        return toDto(savedUser);
    }

    @Override
    public UserDto authenticate(UserLoginDto userLoginDto) {
        Optional<User> userOptional = userRepository.findByUsername(userLoginDto.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
                return toDto(user);
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public boolean setUserRole(String username, UserRole role) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setRole(role);
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    private UserDto toDto(User user) {
        return
            UserDto.builder()
                .surname(user.getSurname())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .role(user.getRole().name())
                .build();
    }

}
