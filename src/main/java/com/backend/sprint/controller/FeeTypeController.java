package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.FeeTypeDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.FeeTypeService;

@RestController
@RequestMapping("fee_types")
public class FeeTypeController {

	@Autowired
	private FeeTypeService service;

	@PostMapping("")
	public FeeTypeDto save(@RequestBody FeeTypeDto dto) {

		if (dto.getId() == 0) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserDto user = (UserDto) auth.getPrincipal();
			dto.setCreatedById(user.getId());
		}
		dto = service.save(dto);

		return dto;
	}

	
}
