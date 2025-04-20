package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Department;
import com.oussama.FacultyPlanning.Model.Faculty;
import com.oussama.FacultyPlanning.Model.Room;
import com.oussama.FacultyPlanning.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findFacultyByUserId(Long userId);

    @Query("SELECT u FROM User u WHERE u.faculty.id = :faculty_id AND u.role = 'TEACHER'")
    List<User> getFacultyTeachers(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT * FROM `room` WHERE faculty_id = :faculty_id", nativeQuery = true)
    List<Room> getFacultyRooms(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT * FROM `department` WHERE faculty_id = :faculty_id", nativeQuery = true)
    List<Department> getFacultyDepartments(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT COUNT(*) FROM `user` WHERE faculty_id = :faculty_id AND role=\"TEACHER\"", nativeQuery = true)
    int countFacultyTeachers(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT COUNT(*) FROM `room` WHERE faculty_id = :faculty_id", nativeQuery = true)
    int countFacultyRooms(@Param("faculty_id") Long facultyId);

    @Query(value = "SELECT COUNT(*) FROM `department` WHERE faculty_id = :faculty_id", nativeQuery = true)
    int countFacultyDepartments(@Param("faculty_id") Long facultyId);
}
