package com.backend.sprint.specifications;

import java.util.List;

public class AthleteSpecificationConstructor<AthleteGroupScheduleDao>
		extends AbstractSpecifictionConstructor<AthleteGroupScheduleDao> {

	public AthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
