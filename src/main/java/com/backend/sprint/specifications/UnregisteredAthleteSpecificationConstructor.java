package com.backend.sprint.specifications;

import java.util.List;

public class UnregisteredAthleteSpecificationConstructor<UnregisteredAthleteDao>
		extends AbstractSpecifictionConstructor<UnregisteredAthleteDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnregisteredAthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
