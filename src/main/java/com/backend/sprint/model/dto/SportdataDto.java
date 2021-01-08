package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SportdataDto {

	private long id;

	private long athleteId;

	private int season;

	private int attitude;

	private int implication;

	private int technicalLevel;

	private int motivations;

	private int outstandingResults;

	private int injuries;

	private String observations;
}
