package com.backend.sprint.specifications;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecifictionConstructor<E> implements Specification<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<String> filters;
	private List<String> booleanOps = Arrays.asList(new String[] { "==" });

	public AbstractSpecifictionConstructor(List<String> filters) {
		this.filters = filters;
	}

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null && filters.size() > 0) {
			result = filters.stream().map(filter -> {

				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2];

				if (operator.equals("==")) {
					return filterPredicateBoolean(root.get(field), builder, operator, value);
				} else {
					return filterPredicateString(root.get(field), builder, operator, value);
				}
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}
		return result;
	}

	protected Predicate filterPredicateBoolean(Path<Boolean> rootField, CriteriaBuilder builder, String operator,
			String value) {
		Predicate predicate = null;

		boolean boolValue = Boolean.parseBoolean(value);
		if (boolValue) {
			predicate = builder.isTrue(rootField);
		} else {
			predicate = builder.isFalse(rootField);
		}
		return predicate;
	}

	protected Predicate filterPredicateString(Path<String> rootField, CriteriaBuilder builder, String operator,
			String value) {
		Predicate predicate = null;

		if (operator.equals("LIKE")) {
			predicate = builder.like(builder.lower(rootField), "%" + value.toLowerCase().trim() + "%");
		} else if (operator.equals("=")) {
			predicate = builder.equal(rootField, value);
		} else if (operator.equals("!=")) {
			predicate = builder.notEqual(rootField, value);
		} else if (operator.equals(">")) {
			predicate = builder.greaterThan(rootField, value);
		} else if (operator.equals(">=")) {
			predicate = builder.greaterThanOrEqualTo(rootField, value);
		} else if (operator.equals("<")) {
			predicate = builder.lessThan(rootField, value);
		} else if (operator.equals("<=")) {
			predicate = builder.lessThanOrEqualTo(rootField, value);
		} else if (operator.toLowerCase().equals("in")) {
			List<Integer> idList = Arrays.asList(value.split(",")).parallelStream().map(d -> Integer.parseInt(d))
					.collect(Collectors.toList());
			predicate = rootField.in(idList);
		}
		return predicate;
	}

}
