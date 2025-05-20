package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Group;
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
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findGroupBySectionId(Long id);

    @Query("SELECT g FROM Group g WHERE g.code = :code AND g.section = :section")
    Optional<Group> findByCodeAndSection(@Param("code") String code, @Param("section") Section section);

    @Query("SELECT g FROM Group g WHERE g.section.department.faculty.id=:faculty_id")
    List<Group> findGroupByFacultyId(@Param("faculty_id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_group WHERE id IN (:ids)", nativeQuery = true)
    void deleteGroupsByIdIn(@Param("ids") List<Long> ids);
}
