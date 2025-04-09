package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findSubjectByFacultyId(Long id);
}
