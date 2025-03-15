package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findFacultyByUserId(Long userId);
}
