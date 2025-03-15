package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findEmailVerificationByToken(String token);
    void deleteEmailVerificationByUserId(Long userId);
}
