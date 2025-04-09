package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String title;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Faculty faculty;
}
