package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleDto {

	private long id;

	private Set<GroupDto> groups;

	private String day;

	private int startHour;

	private int startMinute;

	private int endHour;

	private int endMinute;

	private Date createdAt;

	private Date updatedAt;

}
