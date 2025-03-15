package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
