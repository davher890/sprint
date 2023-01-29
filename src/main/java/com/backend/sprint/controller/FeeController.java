package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.service.FeeService;

@RestController
@RequestMapping("fees")
public class FeeController {

	@Autowired
	private FeeService service;

	@PostMapping("")
	public FeeDto save(@RequestBody FeeDto fee) {
		return service.save(fee);
	}
}
