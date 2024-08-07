package com.backend.sprint.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.LoginDto;

@RestController
@RequestMapping("/login")
public class LoginController {

	@PostMapping("/")
	public void signin(@RequestBody LoginDto login) throws Exception {
		System.out.println("asdad");
	}

}
