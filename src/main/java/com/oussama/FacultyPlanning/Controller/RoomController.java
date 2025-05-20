package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Room;
import com.oussama.FacultyPlanning.Repository.RoomRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final ExcelImportService excelImportService;
    private final RoomRepository roomRepository;

    @PostMapping("/import")
    public ResponseEntity<?> importRooms(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        try {
            List<Room> importedRooms = excelImportService.importRoomsFromExcel(file, Long.valueOf(facultyId));
            return ResponseEntity.ok(importedRooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

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

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteRoomsByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No room IDs provided");
        }
        try {
            roomRepository.deleteRoomsByIdIn(ids);
            return ResponseEntity.ok("Rooms deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete rooms: " + e.getMessage());
        }
    }
}