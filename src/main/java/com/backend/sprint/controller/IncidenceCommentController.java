package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.IncidenceCommentDto;
import com.backend.sprint.service.IncidenceCommentService;
import com.backend.sprint.service.MailService;

@RestController
@RequestMapping("incidence_comments")
public class IncidenceCommentController {

	@Autowired
	private IncidenceCommentService service;

	@Autowired
	private MailService mailService;

	@PostMapping("")
	public IncidenceCommentDto save(@RequestBody IncidenceCommentDto dto) {
		dto = service.save(dto);

		mailService.sendMail("davher890@gmail.com", "Nuevo mensaje en incidencia " + dto.getIncidenceId(),
				"Texto: " + dto.getMessage() + "\n http://sprint-react.s3-website-us-east-1.amazonaws.com/incidences/"
						+ dto.getId());
		return dto;
	}
}
