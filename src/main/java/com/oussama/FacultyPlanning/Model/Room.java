package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oussama.FacultyPlanning.Enum.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;
    private String code;
    @Enumerated
    private RoomType type;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Faculty faculty;
}
