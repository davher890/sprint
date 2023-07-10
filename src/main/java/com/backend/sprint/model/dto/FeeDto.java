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

	private float monthlyFee = 0;

	private float enrollmentFee = 0;

	private float membershipFee = 0;

	private float licenseTerritorialFee = 0;

	private float licenseNationalFee = 0;

	private float quarterOneDay;

	private float quarterTwoDays;

	private float quarterThreeDays;

	private float quarterSpecialization;

	private Date createdAt;

	private Date updatedAt;
}
