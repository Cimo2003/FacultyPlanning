package com.oussama.FacultyPlanning.Password;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ResetCodeService {
    private final ResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<ResetCode> findResetCodeById(Long id) {
        return resetCodeRepository.findById(id);
    }

    public Optional<ResetCode> findResetCodeByEmail(String email) {
        return resetCodeRepository.findResetCodeByEmail(email);
    }

    public ResetCode newResetCode(ResetCode resetCode) {
        return resetCodeRepository.save(resetCode);
    }

    public ResetCode updateResetCode(ResetCode resetCode) {
        return resetCodeRepository.save(resetCode);
    }

    public void deleteResetCode(ResetCode resetCode) {
        resetCodeRepository.delete(resetCode);
    }

    public boolean isCodeValid(String email, String code) {
        ResetCode resetCode = resetCodeRepository.findResetCodeByEmail(email).get();
        int expDate = LocalDateTime.now().compareTo(resetCode.getExpirationDate());
        if (passwordEncoder.matches(code, resetCode.getCode()) && resetCode.getNumTries() < 3 && expDate <= 0) {
            resetCode.setNumTries(resetCode.getNumTries() + 1);
            return true;
        }else{
            resetCode.setNumTries(resetCode.getNumTries() + 1);
            return false;
        }
    }
}
