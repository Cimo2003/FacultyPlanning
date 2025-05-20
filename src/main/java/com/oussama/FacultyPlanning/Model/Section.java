package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oussama.FacultyPlanning.Enum.LVL;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated
    private LVL level;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnoreProperties
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Group> groups;
}
