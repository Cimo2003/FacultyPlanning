package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.groups g WHERE " +
            "(:semesterId IS NULL OR c.semester.id = :semesterId) AND " +
            "(:subjectId IS NULL OR c.subject.id = :subjectId) AND " +
            "(:teacherId IS NULL OR c.user.id = :teacherId) AND " +
            "(:groupId IS NULL OR g.id = :groupId)")
    List<Course> findByCriteria(
            @Param("semesterId") Long semesterId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("groupId") Long groupId);
}
