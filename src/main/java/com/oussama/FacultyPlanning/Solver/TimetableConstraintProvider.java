package com.oussama.FacultyPlanning.Solver;

import com.oussama.FacultyPlanning.Model.Course;
import com.oussama.FacultyPlanning.Model.Group;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                // Hard constraints
                roomConflict(factory),
                teacherConflict(factory),
                groupConflict(factory),
                roomTypeMismatch(factory),
                roomAvailability(factory),

                // Soft constraints
                teacherTimeEfficiency(factory),
                groupTimeEfficiency(factory),
                teacherRoomStability(factory)
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

    // Soft: Teacher schedule should be compact
    private Constraint teacherTimeEfficiency(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTeacher),
                        Joiners.equal(course -> course.getTimeslot().getDay()))
                .filter((course1, course2) -> {
                    Duration between = Duration.between(course1.getTimeslot().getToTime(), course2.getTimeslot().getFromTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(90)) > 0;
                })
                .penalize(HardSoftScore.ONE_SOFT,
                        (course1, course2) -> {
                            Duration between = Duration.between(course1.getTimeslot().getToTime(), course2.getTimeslot().getFromTime());
                            return (int) (between.toMinutes() / 90);
                        })
                .asConstraint("Teacher time efficiency");
    }

    // Soft: Group schedule should be compact
    private Constraint groupTimeEfficiency(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(course -> course.getTimeslot().getDay()),
                        Joiners.filtering((c1, c2) -> {
                            Set<Long> c1GroupIds = c1.getGroups().stream().map(Group::getId).collect(Collectors.toSet());
                            return c2.getGroups().stream().map(Group::getId).anyMatch(c1GroupIds::contains);
                        }))
                .filter((course1, course2) -> {
                    Duration between = Duration.between(course1.getTimeslot().getToTime(), course2.getTimeslot().getFromTime());
                    return !between.isNegative() && between.compareTo(Duration.ofMinutes(90)) > 0;
                })
                .penalize(HardSoftScore.ONE_SOFT,
                        (course1, course2) -> {
                            Duration between = Duration.between(course1.getTimeslot().getToTime(), course2.getTimeslot().getFromTime());
                            return (int) (between.toMinutes() / 90);
                        })
                .asConstraint("Group time efficiency");
    }

    // Soft: A teacher should preferably teach in the same room
    private Constraint teacherRoomStability(ConstraintFactory factory) {
        return factory.forEach(Course.class)
                .groupBy(Course::getTeacher,
                        Course::getRoom)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Teacher room stability");
    }
}