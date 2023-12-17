package com.backend.sprint.utils;

public enum ReceiptStatus {

	OPEN("open"), VALIDATED("validated"), RETURN("return"), NOT_PAID("not_paid"), PAID("paid"), PENDING("pending"),
	SENT_BY_ERROR("sent_by_error");

	public final String label;

	private ReceiptStatus(String label) {
		this.label = label;
	}
}
