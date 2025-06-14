package com.oussama.FacultyPlanning.Solver;

import com.oussama.FacultyPlanning.Model.Timetable;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolverEventListener implements org.optaplanner.core.api.solver.event.SolverEventListener<Timetable> {
    private static final Logger log = LoggerFactory.getLogger(SolverEventListener.class);

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<Timetable> event) {
        Timetable timetable = event.getNewBestSolution();
        log.info("New best score: {}", timetable.getScore());
        log.info("Assigned courses: {}/{}",
                timetable.getCourses().stream().filter(c -> c.getRoom() != null).count(),
                timetable.getCourses().size());
    }
}