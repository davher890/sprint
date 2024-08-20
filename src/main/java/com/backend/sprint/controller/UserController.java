package com.backend.sprint.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.UserService;
import com.backend.sprint.specifications.UserSpecificationConstructor;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("")
	public Page<UserDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		if (user.isAdmin()) {
			return service.findPagintation(new UserSpecificationConstructor<>(filters), pageable, sort, sortDirection);
		} else if (user.isManager()) {
			List<Long> userIds = service.findManagerUserUserIds(user.getId());
			userIds.add(user.getId());
			filters.add("id__in__" + userIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
			return service.findPagintation(new UserSpecificationConstructor<>(filters), pageable, sort, sortDirection);
		} else {
			return Page.empty();
		}
	}

	@GetMapping("/{id}")
	public UserDto findById(@PathVariable long id) {
		return service.findById(id);
	}

	@PostMapping("")
	public UserDto save(@RequestBody UserDto dto) throws Exception {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setUpdatedById(user.getId());

		return service.save(dto);
	}

	@PostMapping("/password")
	public UserDto changePassword(@RequestBody UserDto dto) throws Exception {
		return service.changePassword(dto);
	}

}
