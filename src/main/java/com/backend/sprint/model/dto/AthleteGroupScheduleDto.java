package com.backend.sprint.model.dto;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteGroupScheduleDto {

	private long id;

	private long athleteId;

	private AthleteDto athlete;

	private long groupId;

	private GroupDto group;

	private long scheduleId;

	private ScheduleDto schedule;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
