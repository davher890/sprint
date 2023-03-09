package com.backend.sprint.utils;

public enum ReceiptStatus {

	OPEN("open"), VALIDATED("validated"), RETURN("return"), PAID("paid");

	public final String label;

	private ReceiptStatus(String label) {
		this.label = label;
	}
}
