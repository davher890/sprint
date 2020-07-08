package com.backend.sprint.specifications;

import java.util.List;

public class GroupSpecificationConstructor<GroupDao> extends AbstractSpecifictionConstructor<GroupDao> {

	public GroupSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
