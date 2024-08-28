package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatisticsDto {

	private long userId;

	private int numSportSchools;

	private int numGroups;

}
