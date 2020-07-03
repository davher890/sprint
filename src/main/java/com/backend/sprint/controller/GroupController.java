package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.service.GroupService;

@RestController
@RequestMapping("groups")
public class GroupController {

	@Autowired
	private GroupService service;

	@GetMapping("")
	public List<GroupDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public GroupDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@PostMapping("")
	public GroupDto save(@RequestBody GroupDto dto) {
		return service.save(dto);
	}
}
