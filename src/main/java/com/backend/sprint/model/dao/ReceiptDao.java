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
@Table(name = "receipts")
public class ReceiptDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "sport_school_id")
	private SportSchoolDao sportSchool;

	private String collection;

	@Column(columnDefinition = "boolean default false")
	private boolean january;

	@Column(columnDefinition = "boolean default false")
	private boolean february;

	@Column(columnDefinition = "boolean default false")
	private boolean march;

	@Column(columnDefinition = "boolean default false")
	private boolean april;

	@Column(columnDefinition = "boolean default false")
	private boolean may;

	@Column(columnDefinition = "boolean default false")
	private boolean june;

	@Column(columnDefinition = "boolean default false")
	private boolean july;

	@Column(columnDefinition = "boolean default false")
	private boolean august;

	@Column(columnDefinition = "boolean default false")
	private boolean september;

	@Column(columnDefinition = "boolean default false")
	private boolean october;

	@Column(columnDefinition = "boolean default false")
	private boolean november;

	@Column(columnDefinition = "boolean default false")
	private boolean december;

	@Column(columnDefinition = "boolean default true")
	private boolean active;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
