package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findGroupBySectionId(Long id);
    @Query("SELECT g FROM Group g WHERE g.section.department.faculty.id=:faculty_id")
    List<Group> findGroupByFacultyId(@Param("faculty_id") Long id);
}
