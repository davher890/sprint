package com.backend.sprint.utils;

public enum ReceiptStatus {

	OPEN("open"), VALIDATED("validated"), RETURN("return"), PAID("paid"), SENT_BY_ERROR("sent_by_error");

	public final String label;

	private ReceiptStatus(String label) {
		this.label = label;
	}
}
