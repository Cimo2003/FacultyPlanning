package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Subject;
import com.oussama.FacultyPlanning.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectRepository subjectRepository;
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
}
