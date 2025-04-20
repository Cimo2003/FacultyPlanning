package com.oussama.FacultyPlanning.Dto;

import com.oussama.FacultyPlanning.Enum.Role;
import com.oussama.FacultyPlanning.Model.Faculty;
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
    private String fullName;
    @Email(message = "invalid email")
    private String email;
    private String phone;
    private String password;
    private Role role;
    private Faculty faculty;
    private boolean isEnabled;
}
