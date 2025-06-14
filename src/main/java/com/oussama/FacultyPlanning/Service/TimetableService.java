package com.oussama.FacultyPlanning.Service;

import com.oussama.FacultyPlanning.Model.*;
import com.oussama.FacultyPlanning.Repository.CourseRepository;
import com.oussama.FacultyPlanning.Repository.RoomRepository;
import com.oussama.FacultyPlanning.Repository.TimeslotRepository;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class TimetableService {
    private final SolverManager<Timetable, UUID> solverManager;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;

    public TimetableService(
            SolverManager<Timetable, UUID> solverManager,
            CourseRepository courseRepository,
            RoomRepository roomRepository,
            TimeslotRepository timeslotRepository) {
        this.solverManager = solverManager;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.timeslotRepository = timeslotRepository;
    }

    public Timetable generateTimetable(Semester semester) {
        List<Course> courses = getCoursesForSemester(semester);
        List<Room> rooms = getAvailableRooms(semester.getFaculty());
        List<Timeslot> timeslots = getAllTimeslots();

        Timetable problem = new Timetable(timeslots, rooms, courses);
        UUID problemId = UUID.randomUUID();

        SolverJob<Timetable, UUID> solverJob = solverManager.solve(problemId, problem);
        try {
            return solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed", e);
        }
    }

    public void saveSolutionToDatabase(Timetable timetable) {
        courseRepository.saveAll(timetable.getCourses());
    }

    private List<Course> getCoursesForSemester(Semester semester) {
        return courseRepository.findCourseBySemesterId(semester.getId());
    }

    private List<Room> getAvailableRooms(Faculty faculty) {
        return roomRepository.findRoomByFacultyId(faculty.getId());
    }

    private List<Timeslot> getAllTimeslots() {
        return timeslotRepository.findAll();
    }
}