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

	private int count;

	@NotNull
	private String firstSurname;

	@NotNull
	private String secondSurname;

	private String fatherMail;

	private String motherMail;

	private String fatherPhone;

	private String motherPhone;

	private String fatherDni;

	private String motherDni;

	private long code;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
