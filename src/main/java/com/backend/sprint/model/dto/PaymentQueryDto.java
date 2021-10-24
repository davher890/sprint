package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentQueryDto {

	private boolean membership;

	private boolean enrollment;

	private boolean september;

	// Oct - Nov - Dec
	private boolean q4;

	// Jan - Feb - Mar
	private boolean q1;

	// Apr - May - Jun
	private boolean q2;

	private String refAdeudo;

}
