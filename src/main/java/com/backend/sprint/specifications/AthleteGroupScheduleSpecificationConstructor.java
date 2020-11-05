package com.backend.sprint.specifications;

import java.util.List;

public class AthleteGroupScheduleSpecificationConstructor<AthleteGroupScheduleDao>
		extends AbstractSpecifictionConstructor<AthleteGroupScheduleDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AthleteGroupScheduleSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	// @Override
	// public Predicate toPredicate(Root<AthleteGroupScheduleDao> root,
	// CriteriaQuery<?> query, CriteriaBuilder builder) {
	// Predicate result = null;
	// if (filters != null) {
	// result = filters.stream().map(filter -> {
	//
	// String field = filter.split("__")[0];
	// String operator = filter.split("__")[1];
	// String value = filter.split("__")[2];
	//
	// String entityName = field.split("_")[0];
	// String entityField = field.split("_")[1];
	//
	// Predicate predicate = null;
	// if (operator.equals("LIKE")) {
	// predicate =
	// builder.like(builder.lower(root.get("id").get(entityName).get(entityField)),
	// "%" + value + "%");
	// }
	// if (operator.equals("=")) {
	// predicate =
	// builder.equal(root.get("id").get(entityName).get(entityField), value);
	// }
	//
	// return predicate;
	// }).reduce((c1, c2) -> builder.and(c1, c2)).get();
	// }
	// return result;
	// }

}
