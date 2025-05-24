package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Section;
import com.oussama.FacultyPlanning.Repository.SectionRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionRepository sectionRepository;
    private final ExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importSections(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        System.out.println(facultyId);
        try {
            List<Section> importedSections = excelImportService.importSectionsFromExcel(file, Long.valueOf(facultyId));
            return ResponseEntity.ok(importedSections);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("departments/{id}")
    public ResponseEntity<List<Section>> getDepartmentSections(@PathVariable Long id) {
        return ResponseEntity.ok(sectionRepository.findSectionByDepartmentId(id));
    }

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Section>> getFacultySections(@PathVariable Long id) {
        return ResponseEntity.ok(sectionRepository.findSectionByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Section> getSection(@PathVariable Long id) {
        return ResponseEntity.ok(sectionRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Section> createSection(@RequestBody Section section) {
        return ResponseEntity.ok(sectionRepository.save(section));
    }

    @PatchMapping
    public ResponseEntity<Section> updateSection(@RequestBody Section sectionRequest) {
        Optional<Section> sectionOptional = sectionRepository.findById(sectionRequest.getId());
        if (sectionOptional.isPresent()){
            Section section = sectionOptional.get();
            if (sectionRequest.getName()!=null) section.setName(sectionRequest.getName());
            if (sectionRequest.getLevel()!=null) section.setLevel(sectionRequest.getLevel());
            if (sectionRequest.getDepartment()!=null) section.setDepartment(sectionRequest.getDepartment());
            return ResponseEntity.ok(sectionRepository.save(section));
        } else {
            throw new RuntimeException("section not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(@PathVariable Long id) {
        Optional<Section> section = sectionRepository.findById(id);
        if (section.isPresent()){
            sectionRepository.deleteGroupsBySectionId(id);
            sectionRepository.delete(section.get());
        }
        return ResponseEntity.ok("section deleted successfully!");
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteSectionsByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No Sections IDs provided");
        }
        try {
            sectionRepository.deleteGroupsBySectionIdIn(ids);
            sectionRepository.deleteSectionsByIdIn(ids);
            return ResponseEntity.ok("Sections deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete Sections: " + e.getMessage());
        }
    }
}
