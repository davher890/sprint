package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.ContactDto;
import com.backend.sprint.service.MailService;

@RestController
@RequestMapping("/contact")
public class ContactController {

	@Autowired
	private MailService mailService;

	@PostMapping("/")
	public void contact(@RequestBody ContactDto dto) throws Exception {
		mailService.sendContactMail(dto.getFrom(), dto.getSubject(), dto.getMessage());
	}
}
