package com.backend.sprint.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class AthleteSpecificationConstructor<AthleteDao> extends AbstractSpecifictionConstructor<AthleteDao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	public Specification<AthleteDao> athleteGroupSchedulesSpecification(long groupId, List<Long> scheduleIds) {

		return new Specification<AthleteDao>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<AthleteDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

				Predicate groupPredicate = builder.equal(root.get("group"), groupId);

				for (long scheduleId : scheduleIds) {
					groupPredicate = builder.and(groupPredicate, builder.isMember(scheduleId, root.get("schedules")));
				}
				return groupPredicate;
			}
		};
	}

	@Override
	public Predicate toPredicate(Root<AthleteDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals("groupId")) {
					return builder.equal(root.get("group").get("id"), Long.valueOf(value));
				} else if (field.equals("scheduleIds")) {
					Predicate predicate = builder.conjunction();
					for (String scheduleId : value.split("_")) {
						predicate = builder.and(predicate,
								builder.isMember(Long.valueOf(scheduleId), root.get("schedules")));
					}
					return predicate;
				} else if (field.equals("groupName")) {
					Predicate predicate = builder.like(builder.lower(root.get("group").get("name")),
							"%" + value.toLowerCase().trim() + "%");
					return predicate;
				} else if (field.equals("sportSchoolId")) {
					Predicate predicate = builder.equal(root.get("sportSchool").get("id"), Long.valueOf(value));
					return predicate;
				} else if (field.equals("familyId")) {
					Predicate predicate = builder.equal(root.get("family").get("id"), Long.valueOf(value));
					return predicate;
				} else {
					return super.filterPredicate(root, builder, field, operator, value);
				}
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}

		return result;
	}

}
