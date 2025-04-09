package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Department;
import com.oussama.FacultyPlanning.Repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Department>> getFacultyDepartments(@PathVariable Long id) {
        return ResponseEntity.ok(departmentRepository.findDepartmentByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Department> deleteDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(departmentRepository.save(department));
    }

    @PatchMapping
    public ResponseEntity<Department> updateDepartment(@RequestBody Department departmentRequest) {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentRequest.getId());
        if (departmentOptional.isPresent()){
            Department department = departmentOptional.get();
            if (departmentRequest.getName()!=null) department.setName(departmentRequest.getName());
            return ResponseEntity.ok(departmentRepository.save(department));
        } else {
            throw new RuntimeException("department not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> createFaculty(@PathVariable Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        department.ifPresent(departmentRepository::delete);
        return ResponseEntity.ok("department deleted successfully!");
    }
}
