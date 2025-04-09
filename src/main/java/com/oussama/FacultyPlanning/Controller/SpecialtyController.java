package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Specialty;
import com.oussama.FacultyPlanning.Repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/specialties")
@RequiredArgsConstructor
public class SpecialtyController {
    private final SpecialtyRepository specialtyRepository;

    @GetMapping("departments/{id}")
    public ResponseEntity<List<Specialty>> getDepartmentSpecialties(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyRepository.findSpecialtyByDepartmentId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialty> getSpecialty(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Specialty> createSpecialty(@RequestBody Specialty specialty) {
        return ResponseEntity.ok(specialtyRepository.save(specialty));
    }

    @PatchMapping
    public ResponseEntity<Specialty> updateSpecialty(@RequestBody Specialty specialtyRequest) {
        Optional<Specialty> specialtyOptional = specialtyRepository.findById(specialtyRequest.getId());
        if (specialtyOptional.isPresent()){
            Specialty specialty = specialtyOptional.get();
            if (specialtyRequest.getName()!=null)specialty.setName(specialtyRequest.getName());
            if (specialtyRequest.getLevel()!=null)specialty.setLevel(specialtyRequest.getLevel());
            return ResponseEntity.ok(specialtyRepository.save(specialty));
        } else {
            throw new RuntimeException("specialty not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecialty(@PathVariable Long id) {
        Optional<Specialty> specialty = specialtyRepository.findById(id);
        specialty.ifPresent(specialtyRepository::delete);
        return ResponseEntity.ok("specialty deleted successfully!");
    }
}
