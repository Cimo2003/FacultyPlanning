package com.oussama.FacultyPlanning.Service;

import com.oussama.FacultyPlanning.Model.EmailVerification;
import com.oussama.FacultyPlanning.Repository.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional @RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;

    public EmailVerification addNewEmailVerification(EmailVerification emailVerification) {
        return emailVerificationRepository.save(emailVerification);
    }

    public Optional<EmailVerification> findEmailVerificationByToken(String token) {
        return emailVerificationRepository.findEmailVerificationByToken(token);
    }

    public void deleteEmailVerificationById(Long id) {
        emailVerificationRepository.deleteById(id);
    }

    public void deleteEmailVerificationByUserId(Long id) {
        emailVerificationRepository.deleteEmailVerificationByUserId(id);
    }
}
