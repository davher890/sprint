package com.backend.sprint.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SportdataSpecificationConstructor<SportdataDao> extends AbstractSpecifictionConstructor<SportdataDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SportdataSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	@Override
	public Predicate toPredicate(Root<SportdataDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals("athleteId")) {
					return super.filterPredicateString(root.get("athlete").get("id"), builder, operator, value);
				} else {
					return super.toPredicate(root, query, builder);
				}
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}

		return result;
	}

}
