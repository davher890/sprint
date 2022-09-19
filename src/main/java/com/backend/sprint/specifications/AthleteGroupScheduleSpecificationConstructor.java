package com.backend.sprint.specifications;

import java.util.List;

public class AthleteGroupScheduleSpecificationConstructor<AthleteGroupScheduleDao>
		extends AbstractSpecifictionConstructor<AthleteGroupScheduleDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AthleteGroupScheduleSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
