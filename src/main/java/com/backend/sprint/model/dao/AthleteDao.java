package com.backend.sprint.model.dao;

import java.util.Date;

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

	@ManyToOne
	@JoinColumn(name = "sport_school_id")
	private SportSchoolDao sportSchool;

	@ManyToOne // (cascade = { CascadeType.ALL })
	@JoinColumn(name = "family_id")
	private FamilyDao family;

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

	private String mail;

	private String iban;

	private String payMethod;

	@NotNull
	private String name;

	@NotNull
	private long code;

	private String feeType;

	private int numDays;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
