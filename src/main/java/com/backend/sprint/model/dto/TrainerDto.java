package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainerDto {

	private long id;

	private Set<Long> groupIds;

	private String name;

	private String surname;

	private String dni;

	private String mail;

	private Date createdAt;

	private Date updatedAt;

}
