package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Section;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findSectionByDepartmentId(Long id);

    @Query("SELECT s FROM Section s WHERE s.department.faculty.id=:faculty_id")
    List<Section> findSectionByFacultyId(@Param("faculty_id") Long facultyId);

    @Query("SELECT s FROM Section s WHERE s.department.faculty.id=:faculty_id AND s.name=:name")
    Optional<Section> findSectionByFacultyIdAndName(@Param("faculty_id") Long facultyId,@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g WHERE g.section.id=:id")
    void deleteGroupsBySectionId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g WHERE g.section.id IN (:ids)")
    void deleteGroupsBySectionIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM section WHERE id IN (:ids)", nativeQuery = true)
    void deleteSectionsByIdIn(@Param("ids") List<Long> ids);
}
