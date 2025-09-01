package com.francis.bookshop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UserDto {
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long, contain 1 uppercase letter and 1 special character"
    )
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @Pattern(
            regexp = "^(ADMIN|USER)$",
            message = "Role must be either ADMIN or USER"
    )
    private String role;
    @Email(message = "Invalid email format")
    private String email;
    private String phoneNumber;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    private boolean isUsing2FA;
    private String mfaUri;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String secret;
}
