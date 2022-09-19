package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.IncidenceCommentDto;
import com.backend.sprint.service.IncidenceCommentService;

@RestController
@RequestMapping("incidence_comments")
public class IncidenceCommentController {

	@Autowired
	private IncidenceCommentService service;

	@PostMapping("")
	public IncidenceCommentDto save(@RequestBody IncidenceCommentDto dto) {
		return service.save(dto);
	}
}
