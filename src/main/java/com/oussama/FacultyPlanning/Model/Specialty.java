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
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated
    private LVL level;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
