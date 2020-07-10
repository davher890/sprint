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

import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.service.SportSchoolService;
import com.backend.sprint.specifications.SportSchoolSpecificationConstructor;

@RestController
@RequestMapping("sport_schools")
public class SportSchoolController {

	@Autowired
	private SportSchoolService service;

	@GetMapping("")
	public Page<SportSchoolDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new SportSchoolSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<SportSchoolDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public SportSchoolDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public SportSchoolDto save(@RequestBody SportSchoolDto dto) {
		return service.save(dto);
	}
}
