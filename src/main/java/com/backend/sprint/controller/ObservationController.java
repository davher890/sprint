package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.ObservationDto;
import com.backend.sprint.service.ObservationService;

@RestController
@RequestMapping("observations")
public class ObservationController {

	@Autowired
	private ObservationService service;

	@GetMapping("/all")
	public List<ObservationDto> findAll() {
		return service.findAll();
	}

	@PostMapping("")
	public ObservationDto save(@RequestBody ObservationDto dto) {

		return service.save(dto);
	}

}
