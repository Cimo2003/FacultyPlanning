package com.oussama.FacultyPlanning.Model;

import lombok.Getter;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.Collection;
import java.util.List;

@PlanningSolution
public class Timetable {
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeslotRange")
    private List<Timeslot> timeslots;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    private List<Room> rooms;

    @Getter
    @PlanningEntityCollectionProperty
    private List<Course> courses;

    @Getter
    @PlanningScore
    private HardSoftScore score;

    // Constructors, getters, setters
    public Timetable() {}

    public Timetable(List<Timeslot> timeslots, List<Room> rooms, List<Course> courses) {
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.courses = courses;
    }

}