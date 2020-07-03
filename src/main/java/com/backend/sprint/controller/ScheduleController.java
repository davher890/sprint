package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.service.ScheduleService;

@RestController
@RequestMapping("schedules")
public class ScheduleController {

	@Autowired
	private ScheduleService service;

	@GetMapping("")
	public List<ScheduleDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ScheduleDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public ScheduleDto save(@RequestBody ScheduleDto dto) {
		return service.save(dto);
	}
}
