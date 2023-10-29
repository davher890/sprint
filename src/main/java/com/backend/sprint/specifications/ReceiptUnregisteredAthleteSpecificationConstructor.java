package com.backend.sprint.specifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ReceiptUnregisteredAthleteSpecificationConstructor<ReceiptUnregisteredAthleteDao>
		extends AbstractSpecifictionConstructor<ReceiptUnregisteredAthleteDao> {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReceiptUnregisteredAthleteSpecificationConstructor(List<String> filters) {
		super(filters);
	}

	@Override
	public Predicate toPredicate(Root<ReceiptUnregisteredAthleteDao> root, CriteriaQuery<?> query,
			CriteriaBuilder builder) {

		Predicate result = null;
		if (filters != null) {
			result = filters.stream().map(filter -> {
				String[] filterSplit = filter.split("__");
				String field = filter.split("__")[0];
				String operator = filter.split("__")[1];
				String value = filter.split("__")[2].toLowerCase();

				if (field.equals("scheduleIds")) {

					List<Long> scheduleIds = Arrays.asList(value.split(",")).stream().map(d -> Long.parseLong(d))
							.collect(Collectors.toList());

					return builder.or(scheduleIds.stream().map(d -> builder.like(root.get(field), "%" + d + "%"))
							.collect(Collectors.toList()).toArray(new Predicate[0]));
				} else if (field.equals("lastRegisterDate")) {
					try {
						Date from = dateFormat.parse(value);

						Date to = null;
						if (filterSplit.length > 3) {
							to = dateFormat.parse(filterSplit[3]);
						}
						return filterPredicateDate(root.<Date>get("lastRegisterDate"), builder, operator, from, to);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				} else {
					if (operator.equals("==")) {
						return filterPredicateBoolean(root.get(field), builder, operator, value);
					} else {
						return filterPredicateString(root.get(field), builder, operator, value);
					}
				}
			}).reduce((c1, c2) -> builder.and(c1, c2)).get();
		}

		return result;
	}

}
