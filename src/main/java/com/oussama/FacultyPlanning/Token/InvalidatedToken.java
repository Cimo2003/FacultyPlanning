package com.oussama.FacultyPlanning.Token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
public class InvalidatedToken {

    @Id
    @Column(nullable = false, length = 512)
    private String token;
    @Column(nullable = false, length = 500)
    private Date expiryDate;

    public InvalidatedToken() {}

    public InvalidatedToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
