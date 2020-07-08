package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FamilyDto {

	private long id;

	private Set<Long> athleteIds;

	@NotNull
	private String firstSurname;

	@NotNull
	private String secondSurname;

	private String fatherMail;

	private String motherMail;

	private long code;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
