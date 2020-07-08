package com.backend.sprint.specifications;

import java.util.List;

public class AthleteSpecificationConstructor<AthleteDao> extends AbstractSpecifictionConstructor<AthleteDao> {

	public AthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
