package com.backend.sprint.utils;

public enum LicenseType {

	National("NATIONAL"), Territorial("TERRITORIAL"), NationalTrail("NATIONALTRAIL"),
	TerritorialTrail("TERRITORIALTRAIL");

	public final String label;

	private LicenseType(String label) {
		this.label = label;
	}

}
