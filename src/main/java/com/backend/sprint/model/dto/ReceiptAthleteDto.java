package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReceiptAthleteDto {

	private long id;

	private long sportSchoolId;
	private String sportSchoolName;

	private long familyId;
	private long familyCode;

	private long groupId;
	private String groupName;

	private Set<Long> scheduleIds;
	private Set<ScheduleDto> schedules;
	private String days;
	private Integer numDays;

	// Personal info
	private String name;
	private String firstSurname;
	private String secondSurname;

	// Sport info
	private String category;
	private String dorsalCategory;
	private long dorsalNumber;
	private String license;
	private String licenseType;
	private boolean specialization;

	// Bank info
	private String iban;
	private String paymentType;
	private String feeType;

	private long receiptId;

	private long athleteId;

	// Fee
	private float monthlyFee;
	private float enrollmentFee;
	private float membershipFee;

	private Date lastRegisterDate;
	private Date lastUnregisterDate;

	private String receiptCollection;
	private Date receiptCreatedAt;

	private String status;

	private Date createdAt;
	private Date updatedAt;

}
