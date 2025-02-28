package com.oussama.FacultyPlanning.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RefreshTokenRequest {
    @NotBlank(message = "the refresh token can't be blank or null")
    @NotNull
    private String refreshToken;
}
