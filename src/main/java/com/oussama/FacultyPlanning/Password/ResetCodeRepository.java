package com.oussama.FacultyPlanning.Password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetCodeRepository extends JpaRepository<ResetCode, Long> {
    Optional<ResetCode> findResetCodeByEmail(String email);
}
