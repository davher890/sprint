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

import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.UserService;
import com.backend.sprint.specifications.GroupSpecificationConstructor;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("")
	public Page<UserDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new GroupSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/{id}")
	public UserDto findById(@PathVariable long id) {
		return service.findById(id);
	}

	@PostMapping("")
	public UserDto save(@RequestBody UserDto user) throws Exception {
		return service.save(user);
	}

}
