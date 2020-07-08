package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScheduleDto {

	private long id;

	private Set<Long> groupIds;

	private String day;

	private int startHour;

	private int startMinute;

	private int endHour;

	private int endMinute;

	private Date createdAt;

	private Date updatedAt;

	public ScheduleDto(String day, int startHour, int startMinute, int endHour, int endMinute) {
		this.day = day;
		this.startHour = startHour;
		this.startMinute = startMinute;
		this.endHour = endHour;
		this.endMinute = endMinute;
	}

}
