package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findRoomByFacultyId(Long id);
}
