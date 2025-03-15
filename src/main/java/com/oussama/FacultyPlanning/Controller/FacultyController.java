package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Faculty;
import com.oussama.FacultyPlanning.Repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/faculties")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyRepository facultyRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Faculty>> getUserFaculties(@PathVariable Long id){
        return ResponseEntity.ok(facultyRepository.findFacultyByUserId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyRepository.save(faculty));
    }

    @PatchMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty facultyRequest) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(facultyRequest.getId());
        if (facultyOptional.isPresent()){
            Faculty faculty = facultyOptional.get();
            faculty.setName(facultyRequest.getName());
            faculty.setOpeningTime(facultyRequest.getOpeningTime());
            faculty.setClosingTime(facultyRequest.getClosingTime());
            return ResponseEntity.ok(facultyRepository.save(faculty));
        } else {
            throw new RuntimeException("faculty not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> createFaculty(@PathVariable Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        faculty.ifPresent(facultyRepository::delete);
        return ResponseEntity.ok("faculty deleted successfully!");
    }
}
