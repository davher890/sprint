package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GroupDto {

	private long id;

	private Set<AthleteDto> athletes;

	private Set<ScheduleDto> schedules;

	@NotNull
	private String name;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
