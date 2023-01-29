package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeeDto {

	private long id;

	private long sportSchoolId;

	private float monthlyFee;

	private float enrollmentFee;

	private float membershipFee;

	private float quarterOneDay;

	private float quarterTwoDays;

	private float quarterThreeDays;

	private float quarterSpecialization;

	private Date createdAt;

	private Date updatedAt;
}
