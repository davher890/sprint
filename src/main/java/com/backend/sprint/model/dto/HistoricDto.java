package com.backend.sprint.model.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HistoricDto {

	private long id;

	private long athleteId;

	private String type;

	private LocalDate date;

	private String observations;

	private Date createdAt;

	private Date updatedAt;
}
