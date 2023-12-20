package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

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

	private Set<Long> feeTypeIds;

	private Set<FeeTypeDto> feeTypes;

	private String address;

	private String municipality;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private String errorMessage;

	private long updatedById;

}
