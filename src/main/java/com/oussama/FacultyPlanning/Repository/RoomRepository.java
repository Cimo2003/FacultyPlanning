package com.oussama.FacultyPlanning.Repository;

import com.oussama.FacultyPlanning.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
