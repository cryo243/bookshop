package com.francis.bookshop.entity;

import com.francis.bookshop.utility.AesEncryptingConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    private String password;

    private String name;

    private String surname;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Convert(converter = AesEncryptingConverter.class)
    private String address;

    @Column(name = "phone_number",  unique = true)
    @Convert(converter = AesEncryptingConverter.class)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Convert(converter = AesEncryptingConverter.class)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
}
