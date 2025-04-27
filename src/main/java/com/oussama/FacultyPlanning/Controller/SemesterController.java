package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Semester;
import com.oussama.FacultyPlanning.Repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/semesters")
@RequiredArgsConstructor
public class SemesterController {
    private final SemesterRepository semesterRepository;

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Semester>> getFacultySemesters(@PathVariable Long id) {
        return ResponseEntity.ok(semesterRepository.findSemesterByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemester(@PathVariable Long id) {
        return ResponseEntity.ok(semesterRepository.getReferenceById(id));
    }

    @GetMapping("/faculties/{id}/current")
    public ResponseEntity<Optional<Semester>> getCurrentSemester(@PathVariable Long id) {
        return ResponseEntity.ok(semesterRepository.findCurrentSemesterByFacultyId(id));
    }

    @PostMapping
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        return ResponseEntity.ok(semesterRepository.save(semester));
    }

    @PatchMapping
    public ResponseEntity<Semester> updateSemester(@RequestBody Semester semesterRequest) {
        Optional<Semester> semesterOptional = semesterRepository.findById(semesterRequest.getId());
        if (semesterOptional.isPresent()){
            Semester semester = semesterOptional.get();
            semester.setNumber(semesterRequest.getNumber());
            if (semesterRequest.getSemesterStart()!=null) semester.setSemesterStart(semesterRequest.getSemesterStart());
            if (semesterRequest.getSemesterEnd()!=null) semester.setSemesterEnd(semesterRequest.getSemesterEnd());
            return ResponseEntity.ok(semesterRepository.save(semester));
        } else {
            throw new RuntimeException("semester not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSemester(@PathVariable Long id) {
        Optional<Semester> semester = semesterRepository.findById(id);
        semester.ifPresent(semesterRepository::delete);
        return ResponseEntity.ok("semester deleted successfully!");
    }

    @GetMapping("/faculties/{id}/courses/count")
    public ResponseEntity<Integer> countActiveCourses(@PathVariable Long id) {
        Optional<Semester> semester = semesterRepository.findCurrentSemesterByFacultyId(id);
        return semester.map(value -> ResponseEntity.ok(semesterRepository.countSemesterCourses(value.getId()))).orElseGet(() -> ResponseEntity.ok(0));
    }
}
