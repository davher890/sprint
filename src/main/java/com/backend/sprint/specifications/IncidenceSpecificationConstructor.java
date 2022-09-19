package com.backend.sprint.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class IncidenceSpecificationConstructor<IncidenceDao> extends AbstractSpecifictionConstructor<IncidenceDao> {

	public IncidenceSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	@Override
	public Predicate toPredicate(Root<IncidenceDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals("createdById")) {
					return filterPredicateString(root.get("createdBy").get("id"), builder, operator, value);
				} else {
					return filterPredicateString(root.get(field), builder, operator, value);
				}
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}

		return result;
	}

}
