package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Long id;
    @Enumerated
    private DayOfWeek day;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime fromTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime toTime;
}
