package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM `email_verification` WHERE user_id = :user_id", nativeQuery = true)
    void deleteEmailVerificationByUserId(@Param("user_id") Integer userId);
}
