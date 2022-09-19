package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IncidenceCommentDto {

	private long id;

	private long incidenceId;

	private String message;

	private Date createdAt;

	private long createdById;

	private String createdByName;

	private Date updatedAt;

}
