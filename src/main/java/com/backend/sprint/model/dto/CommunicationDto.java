package com.backend.sprint.model.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommunicationDto {

	private Set<String> to;
	private Set<String> cc;
	private Set<String> cco;

	private String subject;
	private String body;
}
