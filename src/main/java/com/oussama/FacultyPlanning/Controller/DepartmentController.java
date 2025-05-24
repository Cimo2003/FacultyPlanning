package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Department;
import com.oussama.FacultyPlanning.Repository.DepartmentRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final ExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importDepartments(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        try {
            List<Department> importedDepartments = excelImportService.importDepartmentsFromExcel(file, Long.valueOf(facultyId));
            return ResponseEntity.ok(importedDepartments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("faculties/{id}")
    public ResponseEntity<List<Department>> getFacultyDepartments(@PathVariable Long id) {
        return ResponseEntity.ok(departmentRepository.findDepartmentByFacultyId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentRepository.getReferenceById(id));
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
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
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()){
            departmentRepository.deleteGroupsByDepartmentId(id);
            departmentRepository.deleteSectionsByDepartmentId(id);
            departmentRepository.delete(department.get());
        }
        return ResponseEntity.ok("department deleted successfully!");
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteDepartmentsByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No Department IDs provided");
        }
        try {
            departmentRepository.deleteGroupsByDepartmentIdIn(ids);
            departmentRepository.deleteSectionsByDepartmentIdIn(ids);
            departmentRepository.deleteDepartmentsByIdIn(ids);
            return ResponseEntity.ok("Departments deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete Departments: " + e.getMessage());
        }
    }
}
