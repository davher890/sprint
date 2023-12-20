package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeeTypeDto {

	private long id;

	private String name;

	private long createdById;

	private long updatedById;

	private Date createdAt;

	private Date updatedAt;
}
