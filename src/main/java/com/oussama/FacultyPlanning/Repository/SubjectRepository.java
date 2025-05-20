package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Subject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findSubjectByFacultyId(Long id);

    Optional<Subject> findSubjectByTitleAndFacultyId(String title, Long facultyId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subject WHERE id IN (:ids)", nativeQuery = true)
    void deleteSubjectsByIdIn(@Param("ids") List<Long> ids);
}