package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.ReceiptAthleteObservationDto;
import com.backend.sprint.service.ReceiptAthleteObservationService;

@RestController
@RequestMapping("receipt_athlete_observations")
public class ReceiptAthleteObservationController {

	@Autowired
	private ReceiptAthleteObservationService service;

	@GetMapping("/all")
	public List<ReceiptAthleteObservationDto> findAll() {
		return service.findAll();
	}

	@PostMapping("")
	public ReceiptAthleteObservationDto save(@RequestBody ReceiptAthleteObservationDto dto) {
		return service.save(dto);
	}

	@DeleteMapping("/{id}")
	public ReceiptAthleteObservationDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

}
