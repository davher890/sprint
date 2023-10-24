package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "receipt_athletes")
public class ReceiptAthleteDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "receipt_id")
	private ReceiptDao receipt;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "athlete_id")
	private AthleteDao athlete;

	private long code;
	private long familyCode;

	// Fee
	@Column(columnDefinition = "float default 0")
	private float monthlyFee;
	@Column(columnDefinition = "float default 0")
	private float enrollmentFee;
	@Column(columnDefinition = "float default 0")
	private float membershipFee;
	@Column(columnDefinition = "float default 0")
	private float sportSchoolAnnualFee;

	@Column(columnDefinition = "float default 0")
	private float licenseTerritorialFee;
	@Column(columnDefinition = "float default 0")
	private float licenseNationalFee;

	private String days;
	private Integer numDays;

	private Date lastRegisterDate;
	private Date lastUnregisterDate;

	private String status;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
