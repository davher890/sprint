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

	private String category;

	private String attitude;

	private String implication;

	private String technicalLevel;

	private String motivations;

	private String outstandingResults;

	private String injuries;

	private String observations;
}
