package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReceiptAthleteObservationDto {

	private long id;

	private long receiptAthleteId;

	private String text;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private long updatedById;
}
