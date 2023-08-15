package com.backend.sprint.model.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

	private String paymentType;

	private String receiptType;

	private String season;

	private List<String> licenseTypes;

	private LocalDate creationDate;

	private Date startDate;

	private Date endDate;

	private float enrollmentFee;

	private float membershipFee;

	private float monthlyFee;

	private float licenseFee;

	private String status = ReceiptStatus.OPEN.name();

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

}
