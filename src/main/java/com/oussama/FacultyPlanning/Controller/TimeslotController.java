package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Model.Timeslot;
import com.oussama.FacultyPlanning.Repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timeslots")
@RequiredArgsConstructor
public class TimeslotController {
    private final TimeslotRepository timeslotRepository;
    @GetMapping
    public ResponseEntity<List<Timeslot>> getTimeslots() {
        return ResponseEntity.ok(timeslotRepository.findAll());
    }
}
