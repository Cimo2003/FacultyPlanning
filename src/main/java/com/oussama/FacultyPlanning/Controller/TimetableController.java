package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Course;
import com.oussama.FacultyPlanning.Model.Semester;
import com.oussama.FacultyPlanning.Model.Timetable;
import com.oussama.FacultyPlanning.Repository.CourseRepository;
import com.oussama.FacultyPlanning.Repository.SemesterRepository;
import com.oussama.FacultyPlanning.Service.TimetableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timetable")
public class TimetableController {
    private static final Logger log = LoggerFactory.getLogger(TimetableController.class);

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/generate")
    public Timetable generate(@RequestParam Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow();
        List<Course> courses = courseRepository.findCourseBySemesterId(semesterId);
        courses.forEach(course -> {
            course.setRoom(null);
            course.setTimeslot(null);
        });
        courseRepository.saveAll(courses);
        log.info("Generating timetable for faculty: {}", semester.getFaculty().getName());
        log.info("Semester: {}", semester.getId());
        log.info("Number of Courses: {}", courses.size());
        log.info("Number of rooms: {}", semester.getFaculty().getRooms().size());
        Timetable timetable = timetableService.generateTimetable(semester);
        timetableService.saveSolutionToDatabase(timetable);
        return timetable;
    }
}