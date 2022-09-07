package com.backend.sprint.controller.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.UserDto;

@Service
public class Utils {

	public List<String> getFilters(List<String> filters) throws Exception {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		Set<Long> sportSchoolIds = user.getSportSchoolIds();
		if (sportSchoolIds.size() == 0) {
			throw new Exception();
		}
		Set<Long> groupIds = user.getGroupIds();
		if (groupIds != null && groupIds.size() > 0) {
			filters.add("groupId__in__" + groupIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		}
		filters.add(
				"sportSchoolId__in__" + sportSchoolIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		return filters;
	}
}
