package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "athlete_group_schedules")
public class AthleteGroupScheduleDao {

	@EmbeddedId
	AthleteGroupScheduleId id;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
