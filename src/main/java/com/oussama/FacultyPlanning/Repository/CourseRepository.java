package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
