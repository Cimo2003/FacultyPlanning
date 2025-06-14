package com.oussama.FacultyPlanning.Model;

import com.oussama.FacultyPlanning.Enum.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;
import java.util.Set;

@Entity
@PlanningEntity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;
    @Enumerated
    private RoomType type;
    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    @ManyToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 100)
    @JoinTable(name = "course_groups",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            indexes = {
                    @Index(name = "idx_course_group_course", columnList = "course_id"),
                    @Index(name = "idx_course_group_group", columnList = "group_id")
            }
    )
    private Set<Group> groups;
    private String color;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne
    @JoinColumn(name = "timeslot_id")
    private Timeslot timeslot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange", nullable = false)
    public Room getRoom() {
        return room;
    }

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange", nullable = false)
    public Timeslot getTimeslot() {
        return timeslot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
