package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IncidenceDto {

	private long id;

	private String title;

	private String description;

	private String status;

	private Date createdAt;

	private long createdById;

	private Date updatedAt;

}
