package com.backend.sprint.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.IncidenceDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.IncidenceService;
import com.backend.sprint.specifications.IncidenceSpecificationConstructor;

@RestController
@RequestMapping("incidences")
public class IncidenceController {

	@Autowired
	private IncidenceService service;

	@GetMapping("")
	public Page<IncidenceDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		filters.add("createdById__=__" + user.getId());
		return service.findPagintation(new IncidenceSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@PostMapping("")
	public IncidenceDto save(@RequestBody IncidenceDto dto) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setCreatedById(user.getId());
		return service.save(dto);
	}

}
