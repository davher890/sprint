package com.backend.sprint.model.dto;

import java.util.Date;

import com.backend.sprint.utils.ReceiptStatus;

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

	private String name;

	private String collection;
	private String collectionName;

	private Date lastRegisterDate;

	private Date lastUnregisterDate;

	private int year;

	private String status = ReceiptStatus.OPEN.name();

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

}
