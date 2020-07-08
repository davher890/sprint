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

	private long sportSchoolId;

	private long familyId;

	private Set<Long> groupIds;

	private boolean imageAuth;

	private Date birthDate;

	private String gender;

	private String dni;

	private String category;

	private String license;

	private String licenseType;

	private String dorsalCategory;

	private long dorsalNumber;

	private String phone1;

	private String phone2;

	private String phone3;

	private String country;

	private String municipality;

	private String address;

	private String postalCode;

	private String mail;

	private String iban;

	private String payMethod;

	private String name;

	private long code;

	private String feeType;

	private int numDays;

	private Date createdAt;

	private Date updatedAt;

}
