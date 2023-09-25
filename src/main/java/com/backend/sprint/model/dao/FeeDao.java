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
import javax.persistence.OneToOne;
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
@Table(name = "fees")
public class FeeDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "sport_school_id", referencedColumnName = "id")
	private SportSchoolDao sportSchool;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float enrollment;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float equipmentAnnualFee = 0;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float sportSchoolAnnualFee = 0;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float membership;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float quarterOneDay;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float quarterTwoDays;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float quarterThreeDays;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float quarterSpecialization;

	@Column(columnDefinition = "real default 0")
	@NotNull
	private float track;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
