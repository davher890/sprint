package com.backend.sprint.utils;

public enum ReceiptStatus {

	OPEN("open"), VALIDATED("validated"), RETURN("return");

	public final String label;

	private ReceiptStatus(String label) {
		this.label = label;
	}
}
