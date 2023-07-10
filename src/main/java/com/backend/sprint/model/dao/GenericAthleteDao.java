package com.backend.sprint.model.dao;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public class GenericAthleteDao {
	private long sportSchoolId;
	private String sportSchoolName;

	private boolean imageAuth;

	private String observations;

	// Personal info
	private String name;
	private String firstSurname;
	private String secondSurname;
	private LocalDate birthDate;
	private String dni;
	private String gender;
	private String nationality;
	private String birthPlace;

	// Family info
	private long familyId;
	private long familyCode;

	private Long groupId;
	private String groupName;
	private String days;
	private Integer numDays;

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

	private long code;

	private Date lastRegisterDate;
	private Date lastUnregisterDate;

	private boolean active;
	private boolean enrollmentFeePaid;
	private boolean membershipFeePaid;

	@Column(columnDefinition = "float default 0")
	private float licenseNationalFee;
	@Column(columnDefinition = "float default 0")
	private float licenseTerritorialFee;

	private Date createdAt;

	private Date updatedAt;

}
