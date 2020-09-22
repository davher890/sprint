package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FamilyDto {

	private long id;

	private Set<Long> athleteIds;

	private String familiarOneSurname;

	private String familiarTwoSurname;

	private String familiarOneMail;

	private String familiarTwoMail;

	private String familiarOnePhone;

	private String familiarTwoPhone;

	private String familiarOneDni;

	private String familiarTwoDni;

	private long code;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
