package com.backend.sprint.specifications;

import java.util.List;

public class TrainerSpecificationConstructor<TrainerDao> extends AbstractSpecifictionConstructor<TrainerDao> {

	public TrainerSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
