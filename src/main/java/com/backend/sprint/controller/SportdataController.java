
package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.SportdataDto;
import com.backend.sprint.service.SportdataService;

@RestController
@RequestMapping("sportdata")
public class SportdataController {

	@Autowired
	private SportdataService service;

	@PostMapping("")
	public SportdataDto save(@RequestBody SportdataDto dto) {
		return service.save(dto);
	}

}
