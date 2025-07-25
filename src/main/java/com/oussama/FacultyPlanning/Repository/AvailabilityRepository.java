package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
