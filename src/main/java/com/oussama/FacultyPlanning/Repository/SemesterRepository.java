package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
}
