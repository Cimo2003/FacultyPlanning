package com.oussama.FacultyPlanning.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private DayOfWeek day;
    private LocalTime fromTime;
    private LocalTime toTime;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User user;
}
