package com.francis.bookshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MfaVerificationRequest {
    @NotEmpty
    private String username;
    private int code;
}
