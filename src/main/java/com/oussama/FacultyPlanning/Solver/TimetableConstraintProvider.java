package com.oussama.FacultyPlanning.Solver;

import com.oussama.FacultyPlanning.Model.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.util.Set;
import java.util.stream.Collectors;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                roomConflict(factory),
                teacherConflict(factory),
                groupConflict(factory),
                roomTypeMismatch(factory),
                roomAvailability(factory),
//                teacherAvailability(factory)
        };
    }

    // Hard: Room can host only one course per timeslot
    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getRoom),
                        Joiners.equal(Course::getTimeslot))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict");
    }

    // Hard: Teacher can teach only one course per timeslot
    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTeacher),
                        Joiners.equal(Course::getTimeslot))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict");
    }

    // Hard: Group can attend only one course per timeslot
    private Constraint groupConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTimeslot),
                        Joiners.filtering((c1, c2) -> {
                            // Use IDs for comparison
                            Set<Long> c1GroupIds = c1.getGroups().stream()
                                    .map(Group::getId)
                                    .collect(Collectors.toSet());

                            return c2.getGroups().stream()
                                    .map(Group::getId)
                                    .anyMatch(c1GroupIds::contains);
                        }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Group conflict");
    }

    // Hard: Room must match required type
    private Constraint roomTypeMismatch(ConstraintFactory factory) {
        return factory.forEach(Course.class)
                .filter(course -> course.getRoom() != null &&
                        course.getRoom().getType() != course.getType())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room type mismatch");
    }

    // Hard: Room must be available in faculty
    private Constraint roomAvailability(ConstraintFactory factory) {
        return factory.forEach(Course.class)
                .filter(course -> course.getRoom() != null &&
                        !course.getRoom().getFaculty().equals(course.getSemester().getFaculty()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room not available");
    }

    // Soft: Minimize teacher gaps between classes
//    private Constraint teacherAvailability(ConstraintFactory factory) {
//        return factory.forEachUniquePair(Course.class,
//                        Joiners.equal(Course::getTeacher),
//                        Joiners.equal(Course::getTimeslot, (t1, t2) -> t1.getDay() == t2.getDay()))
//                        .filter((c1, c2) -> {
//                            LocalTime gap = calculateTimeGap(c1.getTimeslot(), c2.getTimeslot());
//                            return gap.compareTo(Duration.ofHours(1)) > 0;
//                        })
//                        .penalize(HardSoftScore.ONE_SOFT,
//                                (c1, c2) -> calculateGapPenalty(c1.getTimeslot(), c2.getTimeslot()))
//                        .asConstraint("Teacher time gap");
//    }

    // Helper methods
    private boolean hasCommonGroup(Course c1, Course c2) {
        Set<Long> c1GroupIds = c1.getGroups().stream()
                .map(Group::getId)
                .collect(Collectors.toSet());
        return c2.getGroups().stream()
                .map(Group::getId)
                .anyMatch(c1GroupIds::contains);
    }
}