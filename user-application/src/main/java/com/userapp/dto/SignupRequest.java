package com.userapp.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 8, max = 50, message = "Username must be between 8 and 50 characters")
    private String username;

    @NotBlank
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^.*_.*@.*$", message = "Email must contain both '@' and '_' characters")
    private String email;

    @NotBlank
    @Size(min = 8, max = 12, message = "Password length must be 8 to 12 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$",
            message = "Password must have 1 uppercase, 1 lowercase, 1 special character, and 1 number")
    private String password;

    @NotBlank
    @Pattern(regexp = "^(Standard User|Administrator)$", message = "User Type must be 'Standard User' or 'Administrator'")
    private String userType;
}



