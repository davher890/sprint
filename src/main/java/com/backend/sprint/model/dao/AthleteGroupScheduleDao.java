package com.backend.sprint.model.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	// @EmbeddedId
	// private AthleteGroupScheduleId id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// @ManyToOne
	// @JoinColumn(name = "athlete_id")
	// @Column(name = "athlete_id")
	private long athleteId;

	// @ManyToOne
	// @JoinColumn(name = "group_id")
	// @Column(name = "group_id")
	private long groupId;

	// @ManyToOne
	// @JoinColumn(name = "group_id")
	// @Column(name = "group_id")
	private long scheduleId;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
