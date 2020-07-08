package com.backend.sprint.specifications;

import java.util.List;

public class ScheduleSpecificationConstructor<ScheduleDao> extends AbstractSpecifictionConstructor<ScheduleDao> {

	public ScheduleSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
