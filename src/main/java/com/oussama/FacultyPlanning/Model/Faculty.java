package com.oussama.FacultyPlanning.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private LocalTime openingTime;
    private LocalTime closingTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "faculty")
    private List<Room> rooms;
    @OneToMany(mappedBy = "faculty")
    private List<Department> departments;
    @OneToMany(mappedBy = "faculty")
    private List<User> teachers;
}
