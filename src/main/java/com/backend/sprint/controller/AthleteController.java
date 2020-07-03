package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.service.AthleteService;

@RestController
@RequestMapping("athletes")
public class AthleteController {

	@Autowired
	private AthleteService service;

	@GetMapping("")
	public List<AthleteDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public AthleteDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public AthleteDto save(@RequestBody AthleteDto dto) {
		return service.save(dto);
	}
}
