package com.backend.sprint.specifications;

import java.util.List;

public class GroupSpecificationConstructor<GroupDao> extends AbstractSpecifictionConstructor<GroupDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
