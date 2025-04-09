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
    public ResponseEntity<Optional<Faculty>> getUserFaculties(@PathVariable Long id){
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
            if (facultyRequest.getName()!=null) faculty.setName(facultyRequest.getName());
            if (facultyRequest.getOpeningTime()!=null) faculty.setOpeningTime(facultyRequest.getOpeningTime());
            if (facultyRequest.getClosingTime()!=null) faculty.setClosingTime(facultyRequest.getClosingTime());
            return ResponseEntity.ok(facultyRepository.save(faculty));
        } else {
            throw new RuntimeException("faculty not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFaculty(@PathVariable Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        faculty.ifPresent(facultyRepository::delete);
        return ResponseEntity.ok("faculty deleted successfully!");
    }

    @GetMapping("/{id}/users/count")
    public ResponseEntity<Integer> countTeachers(@PathVariable Long id){
        return ResponseEntity.ok(facultyRepository.countFacultyTeachers(id));
    }

    @GetMapping("/{id}/departments/count")
    public ResponseEntity<Integer> countDepartments(@PathVariable Long id){
        return ResponseEntity.ok(facultyRepository.countFacultyDepartments(id));
    }

    @GetMapping("/{id}/rooms/count")
    public ResponseEntity<Integer> countRooms(@PathVariable Long id){
        return ResponseEntity.ok(facultyRepository.countFacultyRooms(id));
    }
}
