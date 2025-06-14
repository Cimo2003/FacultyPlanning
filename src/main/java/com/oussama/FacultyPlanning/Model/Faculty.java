package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "rooms", "departments", "teachers"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Column(name = "opening_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Room> rooms;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Department> departments;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<User> teachers;
}
