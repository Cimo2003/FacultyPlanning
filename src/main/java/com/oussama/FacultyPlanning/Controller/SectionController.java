package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Section;
import com.oussama.FacultyPlanning.Repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionRepository sectionRepository;

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
        section.ifPresent(sectionRepository::delete);
        return ResponseEntity.ok("section deleted successfully!");
    }
}
