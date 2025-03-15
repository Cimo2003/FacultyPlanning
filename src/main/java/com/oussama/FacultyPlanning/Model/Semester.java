package com.oussama.FacultyPlanning.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int number;
    private LocalDate semesterStart;
    private LocalDate semesterEnd;
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
}
