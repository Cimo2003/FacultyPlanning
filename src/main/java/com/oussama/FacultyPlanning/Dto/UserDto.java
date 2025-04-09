package com.oussama.FacultyPlanning.Dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    @Email(message = "invalid email")
    private String email;
    private String phone;
    private String password;
    private String role;
    private boolean isEnabled;
}
