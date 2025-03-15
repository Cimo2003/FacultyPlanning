package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
}
