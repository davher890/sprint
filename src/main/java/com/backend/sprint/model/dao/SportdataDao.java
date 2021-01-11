package com.backend.sprint.model.dao;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sportdata")
public class SportdataDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "athlete_id")
	private AthleteDao athlete;

	private int season;

	private String attitude;

	private String implication;

	private String technicalLevel;

	private String motivations;

	private String outstandingResults;

	private String injuries;

	private String observations;

}
