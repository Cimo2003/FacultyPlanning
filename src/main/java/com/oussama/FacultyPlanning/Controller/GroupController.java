package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Group;
import com.oussama.FacultyPlanning.Repository.GroupRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupRepository groupRepository;
    private final ExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importGroups(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        try {
            List<Group> importedGroups = excelImportService.importGroupsFromExcel(file, Long.valueOf(facultyId));
            return ResponseEntity.ok(importedGroups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("specialties/{id}")
    public ResponseEntity<List<Group>> getSpecialtyGroups(@PathVariable Long id) {
        return ResponseEntity.ok(groupRepository.findGroupBySectionId(id));
    }

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Group>> getFacultyGroups(@PathVariable Long id) {
        return ResponseEntity.ok(groupRepository.findGroupByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupRepository.save(group));
    }

    @PatchMapping
    public ResponseEntity<Group> updateGroup(@RequestBody Group groupRequest) {
        Optional<Group> subjectOptional = groupRepository.findById(groupRequest.getId());
        if (subjectOptional.isPresent()){
            Group group = subjectOptional.get();
            if (groupRequest.getCode()!=null) group.setCode(groupRequest.getCode());
            return ResponseEntity.ok(groupRepository.save(group));
        } else {
            throw new RuntimeException("group not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        Optional<Group> group = groupRepository.findById(id);
        group.ifPresent(groupRepository::delete);
        return ResponseEntity.ok("group deleted successfully!");
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteGroupsByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No Group IDs provided");
        }
        try {
            groupRepository.deleteGroupsByIdIn(ids);
            return ResponseEntity.ok("Groups deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete Groups: " + e.getMessage());
        }
    }
}
