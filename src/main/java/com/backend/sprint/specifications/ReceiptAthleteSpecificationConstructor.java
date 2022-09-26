package com.backend.sprint.specifications;

import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ReceiptAthleteSpecificationConstructor<ReceiptAthleteDao>
		extends AbstractSpecifictionConstructor<ReceiptAthleteDao> {

	public ReceiptAthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	@Override
	public Predicate toPredicate(Root<ReceiptAthleteDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals("receiptId")) {
					return filterPredicateString(root.get("receipt").get("id"), builder, operator, value);
				} else if (field.equals("name")) {
					return filterPredicateString(root.get("athlete").get("name"), builder, operator, value);
				} else if (field.equals("firstSurname")) {
					return filterPredicateString(root.get("athlete").get("firstSurname"), builder, operator, value);
				} else if (field.equals("secondSurname")) {
					return filterPredicateString(root.get("athlete").get("secondSurname"), builder, operator, value);
				}

				else if (field.equals("active")) {
					return null;
				} else if (field.equals("sportSchoolId")) {
					return null;
				} else if (field.equals("groupId")) {
					return null;
				} else {
					return filterPredicateString(root.get(field), builder, operator, value);
				}
			}).filter(Objects::nonNull).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}

		return result;
	}

}
