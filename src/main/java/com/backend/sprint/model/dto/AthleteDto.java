package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteDto {

	private long id;

	private boolean extern = false;
	private long sportSchoolId;

	// private long familyId;

	private long groupId;
	private Set<Long> scheduleIds;

	private boolean imageAuth;
	private String observations;
	// Personal info
	private String name;
	private String firstSurname;
	private String secondSurname;
	private Date birthDate;
	private String dni;
	private String gender;

	// Family info
	private String familiarOneName;
	private String familiarOneFirstSurname;
	private String familiarOneSecondSurname;
	private String familiarOneDni;
	private String familiarOneMail;

	private String familiarTwoName;
	private String familiarTwoFirstSurname;
	private String familiarTwoSecondSurname;
	private String familiarTwoDni;
	private String familiarTwoMail;

	private long familyCode;

	// Sport info
	private String category;
	private String dorsalCategory;
	private long dorsalNumber;
	private String license;
	private String licenseType;
	private boolean specialization;

	// Contact info
	private String mail;
	private String phone1;
	private String phone2;
	private String phone3;
	private String municipality;
	private String postalCode;
	private String address;

	// Bank info
	private String iban;
	private String paymentType;
	private String feeType;
	private String holderName;
	private String holderFirstSurname;
	private String holderSecondSurname;
	private String holderDni;

	// Code
	private long code;

	private Date createdAt;

	private Date updatedAt;

}
