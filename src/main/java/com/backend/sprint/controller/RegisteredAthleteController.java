package com.backend.sprint.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dao.GenericAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.specifications.RegisteredAthleteSpecificationConstructor;

@RestController
@RequestMapping("registered_athletes")
public class RegisteredAthleteController {

	@Autowired
	private RegisteredAthleteService service;

	@Autowired
	private ExcelService excelService;

	@GetMapping("")
	public Page<AthleteDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		Set<Long> sportSchoolIds = user.getSportSchoolIds();
		if (sportSchoolIds.size() == 0) {
			return Page.empty();
		}
		Set<Long> groupIds = user.getGroupIds();
		if (groupIds != null && groupIds.size() > 0) {
			filters.add("groupId__in__" + groupIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		}
		filters.add(
				"sportSchoolId__in__" + sportSchoolIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		return service.findPagintation(new RegisteredAthleteSpecificationConstructor<>(filters), pageable, sort,
				sortDirection);
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<? extends GenericAthleteDao> athletes = service
				.findDaoPagintation(new RegisteredAthleteSpecificationConstructor<>(filters), pageable).getContent();
		excelService.downloadAthletesExcel(columns, response, athletes, "Atletas");

	}

}
