package com.backend.sprint.model.dao;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class AthleteGroupScheduleId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "athlete_id")
	AthleteDao athlete;

	@ManyToOne
	@JoinColumn(name = "group_id")
	GroupDao group;

	@ManyToOne
	@JoinColumn(name = "schedule_id")
	ScheduleDao schedule;
}
