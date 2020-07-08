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
	private List<String> filters;

	public AbstractSpecifictionConstructor(List<String> filters) {
		this.filters = filters;
	}

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {

				String field = filter.split("__")[0];
				String operation = filter.split("__")[1];
				String value = filter.split("__")[2];

				Predicate predicate = null;
				if (operation.equals("like")) {
					predicate = builder.like(root.get(field), value);
				}
				if (operation.equals("equals")) {
					predicate = builder.equal(root.get(field), value);
				}

				return predicate;
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}
		return result;
	}

}
