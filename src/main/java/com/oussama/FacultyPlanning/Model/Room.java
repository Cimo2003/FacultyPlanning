package com.oussama.FacultyPlanning.Model;

import com.oussama.FacultyPlanning.Enum.RoomType;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Enumerated
    private RoomType type;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}
