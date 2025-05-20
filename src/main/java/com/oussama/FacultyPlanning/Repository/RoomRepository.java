package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findRoomByFacultyId(Long id);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room WHERE id IN (:ids)", nativeQuery = true)
    void deleteRoomsByIdIn(@Param("ids") List<Long> ids);
}
