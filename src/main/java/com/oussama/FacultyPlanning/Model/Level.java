package com.oussama.FacultyPlanning.Model;

import com.oussama.FacultyPlanning.Enum.LVL;
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
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated
    private LVL code;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
