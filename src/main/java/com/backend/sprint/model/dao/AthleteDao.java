package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "athletes")
public class AthleteDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private boolean extern = false;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sport_school_id")
	private SportSchoolDao sportSchool;

	private boolean imageAuth;

	@Column(columnDefinition = "text")
	private String observations;
	// Personal info
	@NotNull
	private String name;
	private String firstSurname;
	private String secondSurname;
	private Date birthDate;
	private String dni;
	private String gender;

	// Family info
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "family_id")
	private FamilyDao family;

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

	@Column(columnDefinition = "boolean default false")
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

	@NotNull
	private long code;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
