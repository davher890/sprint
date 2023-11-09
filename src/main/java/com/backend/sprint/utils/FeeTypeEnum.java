package com.backend.sprint.utils;

public enum FeeTypeEnum {

	CLUB("Entrenamientos"), CLUB_ANUAL("Entrenamientos anual"), FED("Federado"), SOCIO("Socio"), PISTAS("Pistas"),
	PISTAS_FED("Pistas/Federado");

	public final String label;

	private FeeTypeEnum(String label) {
		this.label = label;
	}
}
