package com.backend.sprint.utils;

public enum FeeTypeEnum {

	CLUB("Club"), CLUB_ANUAL("Club anual"), FED("Federado"), SOCIO("Socio"), PISTAS("Pistas"),
	PISTAS_FED("Pistas/Federado");

	public final String label;

	private FeeTypeEnum(String label) {
		this.label = label;
	}
}
