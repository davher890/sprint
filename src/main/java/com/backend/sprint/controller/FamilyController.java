package com.backend.sprint.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.FamilyService;

@RestController
@RequestMapping("families")
public class FamilyController {

	@Autowired
	private FamilyService service;

	@Autowired
	private AthleteService athleteService;

	@GetMapping("")
	public List<FamilyDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public FamilyDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/athletes")
	public Set<AthleteDto> findAthletesByFAmily(@PathVariable int id) {
		return athleteService.findByFamily(id);
	}

	@PostMapping("")
	public FamilyDto save(@RequestBody FamilyDto dto) {
		return service.save(dto);
	}
}
