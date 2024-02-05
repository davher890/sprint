package com.backend.sprint.utils;

public enum MembershipTypeEnum {

	ATHLETE("Atleta"), SUBSIDIZED("Atleta subvencionado"), TRAINER("Entrenador"), DIRECTOR("Junta directiva"),
	MEMBERSHIP("Socio");

	public final String label;

	private MembershipTypeEnum(String label) {
		this.label = label;
	}
}
