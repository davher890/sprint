package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.SignUpDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.UserService;

@RestController
@RequestMapping("/signup")
public class SignupController {

	@Autowired
	private UserService service;

	@PostMapping("/")
	public UserDto signup(@RequestBody SignUpDto dto) throws Exception {
		return service.save(dto);
	}

}
