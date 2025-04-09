package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Room;
import com.oussama.FacultyPlanning.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomRepository roomRepository;

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Room>> getFacultyRooms(@PathVariable Long id) {
        return ResponseEntity.ok(roomRepository.findRoomByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomRepository.save(room));
    }

    @PatchMapping
    public ResponseEntity<Room> updateRoom(@RequestBody Room roomRequest) {
        Optional<Room> roomOptional = roomRepository.findById(roomRequest.getId());
        if (roomOptional.isPresent()){
            Room room = roomOptional.get();
            if (roomRequest.getCode()!=null) room.setCode(roomRequest.getCode());
            if (roomRequest.getType()!=null) room.setType(roomRequest.getType());
            return ResponseEntity.ok(roomRepository.save(room));
        } else {
            throw new RuntimeException("room not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        Optional<Room> room = roomRepository.findById(id);
        room.ifPresent(roomRepository::delete);
        return ResponseEntity.ok("room deleted successfully!");
    }
}
