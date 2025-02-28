package com.oussama.FacultyPlanning.Token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public void blacklistToken(String token, Date expiryDate) {
        invalidatedTokenRepository.save(new InvalidatedToken(token, expiryDate));
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<InvalidatedToken> invalidatedToken = invalidatedTokenRepository.findByToken(token);
        return invalidatedToken.isPresent();
    }
}
