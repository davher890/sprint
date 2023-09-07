package com.backend.sprint.utils;

public enum FeeTypeEnum {

	CLUB("Club"), CLUB_ANUAL("Club anual"), SOCIO("Socio"), PISTAS("Pistas"), FED("Federado"),
	PISTAS_FED("Pistas/Federado");

//	options.add(new SelectOptionDto("PISTAS", "Pistas"));
//	options.add(new SelectOptionDto("LIC_PISTAS", "Licencia/Pistas"));
//	options.add(new SelectOptionDto("ENTRENADOR", "Entrenadores"));
	public final String label;

	private FeeTypeEnum(String label) {
		this.label = label;
	}
}
