package com.backend.sprint.specifications;

import java.util.List;

public class UserSpecificationConstructor<UserDao> extends AbstractSpecifictionConstructor<UserDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
