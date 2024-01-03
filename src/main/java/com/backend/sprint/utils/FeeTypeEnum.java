package com.backend.sprint.utils;

public enum FeeTypeEnum {

	CLUB("Entrenamientos", 1L), CLUB_ANUAL("Entrenamientos anual", 2L), FED("Federado", 3L), SOCIO("Socio", 4L),
	PISTAS("Pistas", 5L), PISTAS_FED("Pistas/Federado", 6L);

	public final String label;

	public final long id;

	private FeeTypeEnum(String label, long id) {
		this.label = label;
		this.id = id;
	}
}
