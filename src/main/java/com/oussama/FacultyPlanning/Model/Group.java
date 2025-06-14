package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.List;

@Entity
@Table(name = "student_group")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;
    private String code;
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
    @ManyToMany(mappedBy = "groups")
    @JsonIgnoreProperties
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Course> courses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        return id != null && id.equals(((Group) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
