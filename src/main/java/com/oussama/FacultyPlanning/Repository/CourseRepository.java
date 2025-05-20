package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.groups g WHERE " +
            "(:semesterId IS NULL OR c.semester.id = :semesterId) AND " +
            "(:subjectId IS NULL OR c.subject.id = :subjectId) AND " +
            "(:teacherId IS NULL OR c.teacher.id = :teacherId) AND " +
            "(:groupId IS NULL OR g.id = :groupId)")
    List<Course> findByCriteria(
            @Param("semesterId") Long semesterId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("groupId") Long groupId
    );

    List<Course> findCourseBySemesterId(Long semesterId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM course WHERE id IN (:ids)", nativeQuery = true)
    void deleteCoursesByIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM course_groups WHERE course_id = :courseId", nativeQuery = true)
    void deleteCourseGroups(@Param("courseId") Long courseId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM course_groups WHERE course_id IN (:ids)", nativeQuery = true)
    void deleteCourseGroupsByIdsIn(@Param("ids") List<Long> ids);

    @Query("SELECT c FROM Course c WHERE c.room.id = :roomId AND c.timeslot.id = :timeslotId AND c.semester.id = :semesterId")
    List<Course> findCoursesByRoomIdAndTimeslotId(
            @Param("semesterId") Long semesterId,
            @Param("roomId") Long roomId,
            @Param("timeslotId") Long timeslotId);

    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId AND c.timeslot.id = :timeslotId AND c.semester.id = :semesterId")
    List<Course> findCoursesByTeacherIdAndTimeslotId(
            @Param("semesterId") Long semesterId,
            @Param("teacherId") Long teacherId,
            @Param("timeslotId") Long timeslotId);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.groups g WHERE g.id IN :groupIds AND c.timeslot.id = :timeslotId AND c.semester.id = :semesterId")
    List<Course> findCoursesByGroupIdsAndTimeslotId(
            @Param("semesterId") Long semesterId,
            @Param("groupIds") List<Long> groupIds,
            @Param("timeslotId") Long timeslotId);

}

