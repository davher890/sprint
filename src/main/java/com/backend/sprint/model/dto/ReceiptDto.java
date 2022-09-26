package com.backend.sprint.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReceiptDto {

	private long id;

	private long sportSchoolId;
	private String sportSchoolName;

	private String collection;

	private boolean january = false;

	private boolean february = false;

	private boolean march = false;

	private boolean april = false;

	private boolean may = false;

	private boolean june = false;

	private boolean july = false;

	private boolean august = false;

	private boolean september = false;

	private boolean october = false;

	private boolean november = false;

	private boolean december = false;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

}
