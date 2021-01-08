package com.backend.sprint.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecifictionConstructor<E> implements Specification<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<String> filters;

	public AbstractSpecifictionConstructor(List<String> filters) {
		this.filters = filters;
	}

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {

				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				return filterPredicate(root, builder, field, operator, value);
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}
		return result;
	}

	protected Predicate filterPredicate(Root<E> root, CriteriaBuilder builder, String field, String operator,
			String value) {
		Predicate predicate = null;
		if (operator.equals("LIKE")) {
			predicate = builder.like(builder.lower(root.get(field)), "%" + value + "%");
		} else if (operator.equals("==")) {
			boolean boolValue = Boolean.parseBoolean(value);
			if (boolValue) {
				predicate = builder.isTrue(root.get(field));
			} else {
				predicate = builder.isFalse(root.get(field));
			}
		} else if (operator.equals("=")) {
			predicate = builder.equal(root.get(field), value);
		} else if (operator.equals("!=")) {
			predicate = builder.notEqual(root.get(field), value);
		} else if (operator.equals(">")) {
			predicate = builder.greaterThan(root.get(field), value);
		} else if (operator.equals(">=")) {
			predicate = builder.greaterThanOrEqualTo(root.get(field), value);
		} else if (operator.equals("<")) {
			predicate = builder.lessThan(root.get(field), value);
		} else if (operator.equals("<=")) {
			predicate = builder.lessThanOrEqualTo(root.get(field), value);
		}

		return predicate;
	}

}
