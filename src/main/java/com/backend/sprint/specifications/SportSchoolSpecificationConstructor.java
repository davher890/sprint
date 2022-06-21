package com.backend.sprint.specifications;

import java.util.List;

public class SportSchoolSpecificationConstructor<SportSchoolDao>
		extends AbstractSpecifictionConstructor<SportSchoolDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SportSchoolSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
