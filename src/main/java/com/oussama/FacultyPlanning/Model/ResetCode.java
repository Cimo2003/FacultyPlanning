package com.oussama.FacultyPlanning.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetCode {
    public ResetCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank(message = "the email can't be blank or null")
    private String email;
    private String code;
    private int numTries = 0;
    private LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);
}

