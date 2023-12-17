package com.backend.sprint.utils;

public enum PaymentType {

	Efectivo("MET"), Transferencia("TRANSF"), DomiciliaciónBancaria("DOM"), Subvencionado("SUBV"),
	EfectivoYTransferencia("METTRANSF");

	public final String label;

	private PaymentType(String label) {
		this.label = label;
	}
}
