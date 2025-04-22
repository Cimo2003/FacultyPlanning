package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findSectionByDepartmentId(Long id);
    @Query("SELECT s FROM Section s WHERE s.department.faculty.id=:faculty_id")
    List<Section> findSectionByFacultyId(@Param("faculty_id") Long facultyId);
}
