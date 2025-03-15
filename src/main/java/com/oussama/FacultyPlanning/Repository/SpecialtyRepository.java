package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
