package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Department;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findDepartmentByFacultyId(Long facultyId);

    @Query("SELECT d FROM Department d WHERE d.faculty.id=:faculty_id AND d.name=:name")
    Optional<Department> findDepartmentByFacultyIdAndName(Long facultyId, String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g WHERE g.section.department.id=:id")
    void deleteGroupsByDepartmentId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Group g WHERE g.section.department.id IN (:ids)")
    void deleteGroupsByDepartmentIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Section s WHERE s.department.id=:id")
    void deleteSectionsByDepartmentId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Section s WHERE s.department.id IN (:ids)")
    void deleteSectionsByDepartmentIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM department WHERE id IN (:ids)", nativeQuery = true)
    void deleteDepartmentsByIdIn(@Param("ids") List<Long> ids);
}
