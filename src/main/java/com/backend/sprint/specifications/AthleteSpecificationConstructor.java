package com.backend.sprint.specifications;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.backend.sprint.utils.HistoricType;

public class AthleteSpecificationConstructor<AthleteGroupScheduleDao>
		extends AbstractSpecifictionConstructor<AthleteGroupScheduleDao> {

	public AthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	@Override
	public Predicate toPredicate(Root<AthleteGroupScheduleDao> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals(HistoricType.REGISTER.name())) {
					Predicate predicate = builder.or(
							builder.and(builder.isNull(root.get("lastRegisterDate")),
									builder.or(builder.isNull(root.get("lastUnregisterDate")),
											builder.greaterThan(root.get("lastUnregisterDate"),
													builder.currentDate()))),
							builder.and(builder.isNotNull(root.get("lastRegisterDate")),
									builder.lessThanOrEqualTo(root.get("lastRegisterDate"), builder.currentDate()),
									builder.or(builder.isNull(root.get("lastUnregisterDate")), builder
											.lessThan(root.get("lastUnregisterDate"), root.get("lastRegisterDate")))));
					return predicate;
				}

				else if (field.equals(HistoricType.UNREGISTER.name())) {
					Predicate predicate = builder.not(builder.or(
							builder.and(builder.isNull(root.get("lastRegisterDate")),
									builder.or(builder.isNull(root.get("lastUnregisterDate")),
											builder.greaterThan(root.get("lastUnregisterDate"),
													builder.currentDate()))),
							builder.and(builder.isNotNull(root.get("lastRegisterDate")),
									builder.lessThanOrEqualTo(root.get("lastRegisterDate"), builder.currentDate()),
									builder.or(builder.isNull(root.get("lastUnregisterDate")), builder
											.lessThan(root.get("lastUnregisterDate"), root.get("lastRegisterDate"))))));
					return predicate;
				}

				else if (field.equals("sportSchoolId")) {
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
