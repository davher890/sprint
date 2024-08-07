package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContactDto {

	private String from;

	private String subject;

	private String message;

}
