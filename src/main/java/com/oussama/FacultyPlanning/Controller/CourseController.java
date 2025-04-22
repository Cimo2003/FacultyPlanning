package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Course;
import com.oussama.FacultyPlanning.Repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isPresent()){
            return ResponseEntity.ok(courseOptional.get());
        } else {
            throw new RuntimeException("course not found");
        }
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@RequestBody Course courseDetails) {
        Optional<Course> courseOptional = courseRepository.findById(courseDetails.getId());
        if (courseOptional.isPresent()){
            Course course = courseOptional.get();
            if (courseDetails.getType()!=null) course.setType(courseDetails.getType());
            if (courseDetails.getUser()!=null) course.setUser(courseDetails.getUser());
            if (courseDetails.getGroups()!=null) course.setGroups(courseDetails.getGroups());
            if (courseDetails.getSubject()!=null) course.setSubject(courseDetails.getSubject());
            if (courseDetails.getDay()!=null) course.setDay(courseDetails.getDay());
            if (courseDetails.getStartTime()!=null) course.setStartTime(courseDetails.getStartTime());
            if (courseDetails.getEndTime()!=null) course.setEndTime(courseDetails.getEndTime());
            if(courseDetails.getHours_per_group()!=0) course.setHours_per_group(courseDetails.getHours_per_group());
            return ResponseEntity.ok(courseRepository.save(course));
        } else {
            throw new RuntimeException("course not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        course.ifPresent(courseRepository::delete);
        return ResponseEntity.ok("course deleted successfully!");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Course>> filterCourses(
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long groupId) {
        List<Course> courses = courseRepository.findByCriteria(semesterId, subjectId, teacherId, groupId);
        return ResponseEntity.ok(courses);
    }
}
