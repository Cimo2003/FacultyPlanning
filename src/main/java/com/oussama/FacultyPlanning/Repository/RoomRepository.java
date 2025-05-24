package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findRoomByFacultyId(Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room WHERE id IN (:ids)", nativeQuery = true)
    void deleteRoomsByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM Room r WHERE r.code=:code AND r.faculty.id=:faculty_id")
    Optional<Object> findByCodeAndFacultyId(@Param("code") String code, @Param("faculty_id") Long facultyId);
}
