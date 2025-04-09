package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findSemesterByFacultyId(Long id);

    @Query(value = "SELECT COUNT(*) FROM `course` WHERE semester_id = :semester_id", nativeQuery = true)
    int countSemesterCourses(@Param("semester_id") Long semesterId);

    @Query(value = "SELECT * FROM `semester` WHERE faculty_id = :faculty_id AND CURRENT_DATE BETWEEN semester_start AND semester_end", nativeQuery = true)
    Optional<Semester> findCurrentSemesterByFacultyId(@Param("faculty_id") Long facultyId);
}
