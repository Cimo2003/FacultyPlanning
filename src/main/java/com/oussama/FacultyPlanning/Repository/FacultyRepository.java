package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findFacultyByUserId(Long userId);

    @Query(value = "SELECT COUNT(*) FROM `user` WHERE faculty_id=1 AND role=\"TEACHER\"", nativeQuery = true)
    int countFacultyTeachers(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT COUNT(*) FROM `room` WHERE faculty_id = :faculty_id", nativeQuery = true)
    int countFacultyRooms(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT COUNT(*) FROM `department` WHERE faculty_id = :faculty_id", nativeQuery = true)
    int countFacultyDepartments(@Param("faculty_id") Long facultyId);
}
