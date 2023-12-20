package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public class GenericReceiptAthleteDao {

	private long sportSchoolId;
	private String sportSchoolName;

	private long groupId;
	private String groupName;
	private boolean specialization;

	// Personal info
	private String name;
	private String firstSurname;
	private String secondSurname;

	// Bank info
	private String paymentType;
	private long feeTypeId;
	private String feeTypeName;

	private long receiptId;
	private long athleteId;
	private long code;

	// Fee
	private int monthlyFee;
	private int enrollmentFee;
	private int membershipFee;
	private int sportSchoolAnnualFee;
	private float licenseTerritorialFee;
	private float licenseNationalFee;

	private String receiptName;
	private String season;

	private String days;
	private Integer numDays;

	private boolean active;

	private String status;

	private Date createdAt;
	private Date updatedAt;

}
