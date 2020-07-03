package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import com.backend.sprint.model.dao.FamilyDao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteDto {

	private long id;

	private FamilyDao family;

	private Set<GroupDto> groups;

	private boolean imageAuth;

	private Date birthDate;

	private String gender;

	private String dni;

	private String category;

	private String licence;

	private String phone1;

	private String phone2;

	private String phone3;

	private String country;

	private String municipality;

	private String address;

	private String fatherMail;

	private String motherMail;

	private String mail;

	private String iban;

	private String payMethod;

	private String name;

	private String surname;

	private String lastName;

	private Date createdAt;

	private Date updatedAt;

}
