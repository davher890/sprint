package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.ScheduleService;
import com.backend.sprint.specifications.ScheduleSpecificationConstructor;

@RestController
@RequestMapping("schedules")
public class ScheduleController {

	@Autowired
	private ScheduleService service;

	@GetMapping("")
	public Page<ScheduleDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		return service.findPagintation(new ScheduleSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/all")
	public List<ScheduleDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ScheduleDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public ScheduleDto save(@RequestBody ScheduleDto dto) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setUpdatedById(user.getId());

		return service.save(dto);
	}
}
