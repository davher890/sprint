package com.backend.sprint.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.IncidenceCommentDto;
import com.backend.sprint.model.dto.IncidenceDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.IncidenceCommentService;
import com.backend.sprint.service.IncidenceService;
import com.backend.sprint.service.MailService;
import com.backend.sprint.service.UserService;

@RestController
@RequestMapping("incidence_comments")
public class IncidenceCommentController {

	@Autowired
	private IncidenceCommentService service;

	@Autowired
	private IncidenceService incidenceService;

	@Autowired
	private UserService userService;

	@Autowired
	private MailService mailService;

	@PostMapping("")
	public IncidenceCommentDto save(@RequestBody IncidenceCommentDto dto) {
		dto = service.save(dto);

		IncidenceDto incidence = incidenceService.findById(dto.getIncidenceId());

		String subject = "Nuevo mensaje en incidencia " + dto.getIncidenceId();

		String body = "Incidencia: " + incidence.getTitle() + "\n\nTexto: " + dto.getMessage()
				+ "\n\nhttp://sportclubsmanager.com/incidences/" + incidence.getId();

		mailService.sendMail(Set.of("davher890@gmail.com"), subject, body);

		UserDto user = userService.findById(incidence.getCreatedById());
		if (user != null) {
			mailService.sendMail(Set.of(user.getEmail()), subject, body);
		}
		return dto;
	}
}
