package com.backend.sprint.specifications;

import java.util.List;

public class RegisteredAthleteSpecificationConstructor<RegisteredAthleteDao>
		extends AbstractSpecifictionConstructor<RegisteredAthleteDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegisteredAthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
