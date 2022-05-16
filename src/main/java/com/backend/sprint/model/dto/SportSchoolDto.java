package com.backend.sprint.model.dto;

import java.util.Date;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SportSchoolDto {

	private long id;

	@NotNull
	private String name;

	private String address;

	private String municipality;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private String errorMessage;

	private long updatedById;

}
