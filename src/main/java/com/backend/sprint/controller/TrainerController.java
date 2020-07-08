package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.service.TrainerService;
import com.backend.sprint.specifications.TrainerSpecificationConstructor;

@RestController
@RequestMapping("trainers")
public class TrainerController {

	@Autowired
	private TrainerService service;

	@GetMapping("")
	public Page<TrainerDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new TrainerSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<TrainerDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public TrainerDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public TrainerDto save(@RequestBody TrainerDto dto) {
		return service.save(dto);
	}
}
