package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "families")
public class FamilyDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private String familiarOneSurname;

	@NotNull
	private String familiarTwoSurname;

	private String familiarOneMail;

	private String familiarTwoMail;

	private String familiarOnePhone;

	private String familiarTwoPhone;

	private String familiarOneDni;

	private String familiarTwoDni;

	private long code;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
