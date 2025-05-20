package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Room;
import com.oussama.FacultyPlanning.Model.Subject;
import com.oussama.FacultyPlanning.Repository.SubjectRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectRepository subjectRepository;
    private final ExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importSubjects(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        try {
            List<Subject> importedSubjects = excelImportService.importSubjectsFromExcel(file, Long.valueOf(facultyId));
            return ResponseEntity.ok(importedSubjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Subject>> getFacultySubjects(@PathVariable Long id) {
        return ResponseEntity.ok(subjectRepository.findSubjectByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubject(@PathVariable Long id) {
        return ResponseEntity.ok(subjectRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    @PatchMapping
    public ResponseEntity<Subject> updateSubject(@RequestBody Subject subjectRequest) {
        Optional<Subject> subjectOptional = subjectRepository.findById(subjectRequest.getId());
        if (subjectOptional.isPresent()){
            Subject subject = subjectOptional.get();
            if (subjectRequest.getTitle()!=null) subject.setTitle(subjectRequest.getTitle());
            if (subjectRequest.getCode()!=null) subject.setCode(subjectRequest.getCode());
            return ResponseEntity.ok(subjectRepository.save(subject));
        } else {
            throw new RuntimeException("subject not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        subject.ifPresent(subjectRepository::delete);
        return ResponseEntity.ok("subject deleted successfully!");
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteSubjectsByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No subject IDs provided");
        }
        try {
            subjectRepository.deleteSubjectsByIdIn(ids);
            return ResponseEntity.ok("Subjects deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete subjects: " + e.getMessage());
        }
    }
}
