package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Course;
import com.oussama.FacultyPlanning.Model.Faculty;
import com.oussama.FacultyPlanning.Model.Group;
import com.oussama.FacultyPlanning.Model.Semester;
import com.oussama.FacultyPlanning.Repository.CourseRepository;
import com.oussama.FacultyPlanning.Repository.FacultyRepository;
import com.oussama.FacultyPlanning.Repository.SemesterRepository;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final ExcelImportService excelImportService;

    @PostMapping("/import")
    public ResponseEntity<?> importCourses(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") String facultyId) {
        try {
            excelImportService.importCourses(file, Long.valueOf(facultyId));
            return ResponseEntity.ok("Courses Imported Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Import failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isPresent()){
            return ResponseEntity.ok(courseOptional.get());
        } else {
            throw new RuntimeException("course not found");
        }
    }

    @GetMapping("/semesters/{id}")
    public ResponseEntity<List<Course>> getCourseBySemesterId(@PathVariable Long id) {
        return ResponseEntity.ok(courseRepository.findCourseBySemesterId(id));
    }

    @GetMapping("/semesters/{semesterId}/users/{userId}")
    public ResponseEntity<List<Course>> getTeacherCourses(@PathVariable("userId") Long userId , @PathVariable("semesterId") Long semesterId) {
        return ResponseEntity.ok(courseRepository.findCourseByTeacherIdAndSemesterId(userId, semesterId));
    }

    @GetMapping("/users/{id}/count")
    public ResponseEntity<Integer> getTeacherCourses(@PathVariable Long id) {
        return ResponseEntity.ok(courseRepository.countTeacherCoursesForToday(id));
    }

    @GetMapping("faculties/{id}/semesters/current")
    public ResponseEntity<List<Course>> getCurrentFacultyCourses(@PathVariable Long id) {
        Optional<Semester> semesterOptional = semesterRepository.findCurrentSemesterByFacultyId(id);
        if(semesterOptional.isPresent()){
            return ResponseEntity.ok(courseRepository.findCourseBySemesterId(semesterOptional.get().getId()));
        } else {
            throw new RuntimeException("semester not found");
        }
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PatchMapping
    public ResponseEntity<Course> updateCourse(@RequestBody Course courseDetails) {
        Optional<Course> courseOptional = courseRepository.findById(courseDetails.getId());
        if (courseOptional.isPresent()){
            Course course = courseOptional.get();
            if (courseDetails.getType()!=null) course.setType(courseDetails.getType());
            if (courseDetails.getTeacher()!=null) course.setTeacher(courseDetails.getTeacher());
            if (courseDetails.getGroups()!=null) course.setGroups(courseDetails.getGroups());
            if (courseDetails.getSubject()!=null) course.setSubject(courseDetails.getSubject());
            if (courseDetails.getColor()!=null) course.setColor(courseDetails.getColor());
            return ResponseEntity.ok(courseRepository.save(course));
        } else {
            throw new RuntimeException("course not found");
        }
    }

    @PatchMapping("/unassign")
    public ResponseEntity<List<Course>> unassignAll(@RequestParam("semester_id") Long semesterId){
        List<Course> courses = courseRepository.findCourseBySemesterId(semesterId);
        courses.forEach(course -> {
            course.setRoom(null);
            course.setTimeslot(null);
        });
        return ResponseEntity.ok(courseRepository.saveAll(courses));
    }

    @PatchMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody Course courseDetails) {
        Optional<Course> courseOptional = courseRepository.findById(courseDetails.getId());
        if (courseOptional.isEmpty()) {
            throw new RuntimeException("course not found");
        }

        Course course = courseOptional.get();

        // Skip conflict checks if timeslot is null (unassigning)
        if (courseDetails.getTimeslot() == null) {
            course.setTimeslot(null);
            course.setRoom(null);
            return ResponseEntity.ok(courseRepository.save(course));
        }

        if(courseDetails.getRoom().getType()!=courseDetails.getType()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Room type conflict");
        }

        // Check for conflicts
        Map<String, List<String>> conflicts = checkConflicts(courseDetails, course.getId());

        if (!conflicts.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(conflicts);
        }

        // No conflicts, proceed with assignment
        course.setTimeslot(courseDetails.getTimeslot());
        course.setRoom(courseDetails.getRoom());
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()){
            courseRepository.deleteCourseGroups(id);
            courseRepository.delete(course.get());
            return ResponseEntity.ok("course deleted successfully!");
        }
        else throw new RuntimeException("course not found");
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<String> deleteCoursesByIds(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No courses IDs provided");
        }
        try {
            courseRepository.deleteCourseGroupsByIdsIn(ids);
            courseRepository.deleteCoursesByIdIn(ids);
            return ResponseEntity.ok("Courses deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete courses: " + e.getMessage());
        }
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

    private Map<String, List<String>> checkConflicts(Course courseDetails, Long currentCourseId) {
        Map<String, List<String>> conflicts = new HashMap<>();

        // Skip if timeslot is null
        if (courseDetails.getTimeslot() == null) {
            return conflicts;
        }

        Long semesterId = courseDetails.getSemester().getId();
        Long timeslotId = courseDetails.getTimeslot().getId();
        Long roomId = courseDetails.getRoom() != null ? courseDetails.getRoom().getId() : null;
        Long teacherId = courseDetails.getTeacher() != null ? courseDetails.getTeacher().getId() : null;
        List<Long> groupIds = courseDetails.getGroups() != null ?
                courseDetails.getGroups().stream().map(Group::getId).collect(Collectors.toList()) :
                new ArrayList<>();

        // Check for room conflicts
        if (roomId != null) {
            List<Course> roomConflicts = courseRepository.findCoursesByRoomIdAndTimeslotId(semesterId, roomId, timeslotId);
            // Filter out the current course from results
            roomConflicts = roomConflicts.stream()
                    .filter(c -> !c.getId().equals(currentCourseId))
                    .toList();

            if (!roomConflicts.isEmpty()) {
                conflicts.put("roomConflicts", roomConflicts.stream()
                        .map(c -> c.getSubject().getTitle())
                        .collect(Collectors.toList()));
            }
        }

        // Check for teacher conflicts
        if (teacherId != null) {
            List<Course> teacherConflicts = courseRepository.findCoursesByTeacherIdAndTimeslotId(semesterId, teacherId, timeslotId);
            // Filter out the current course from results
            teacherConflicts = teacherConflicts.stream()
                    .filter(c -> !c.getId().equals(currentCourseId))
                    .toList();

            if (!teacherConflicts.isEmpty()) {
                conflicts.put("teacherConflicts", teacherConflicts.stream()
                        .map(c -> c.getSubject().getTitle())
                        .collect(Collectors.toList()));
            }
        }

        // Check for group conflicts
        if (!groupIds.isEmpty()) {
            List<Course> groupConflicts = courseRepository.findCoursesByGroupIdsAndTimeslotId(semesterId, groupIds, timeslotId);
            // Filter out the current course from results
            groupConflicts = groupConflicts.stream()
                    .filter(c -> !c.getId().equals(currentCourseId))
                    .toList();

            if (!groupConflicts.isEmpty()) {
                conflicts.put("groupConflicts", groupConflicts.stream()
                        .map(c -> c.getSubject().getTitle())
                        .collect(Collectors.toList()));
            }
        }

        return conflicts;
    }
}
