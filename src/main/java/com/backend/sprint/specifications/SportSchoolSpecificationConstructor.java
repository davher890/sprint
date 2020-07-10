package com.backend.sprint.specifications;

import java.util.List;

public class SportSchoolSpecificationConstructor<SportSchoolDao>
		extends AbstractSpecifictionConstructor<SportSchoolDao> {

	public SportSchoolSpecificationConstructor(List<String> filters) {
		super(filters);
	}

}
