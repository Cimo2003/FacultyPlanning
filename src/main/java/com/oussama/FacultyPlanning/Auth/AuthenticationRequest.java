package com.oussama.FacultyPlanning.Auth;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data @Builder
public class AuthenticationRequest {
    @Email(message = "invalid email")
    private String username;
    @Length(min = 8,max = 20 , message = "password must be between 8 and 20")
    private String password;
}
