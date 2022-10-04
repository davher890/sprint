package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.controller.utils.Utils;
import com.backend.sprint.model.dao.UnregisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.UnregisteredAthleteService;
import com.backend.sprint.specifications.UnregisteredAthleteSpecificationConstructor;

@RestController
@RequestMapping("unregistered_athletes")
public class UnregisteredAthleteController {

	@Autowired
	private UnregisteredAthleteService service;

	@Autowired
	private ExcelService excelService;

	@Autowired
	private Utils utils;

	@GetMapping("")
	public Page<AthleteDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return service.findPagintation(new UnregisteredAthleteSpecificationConstructor<>(filters), pageable, sort,
				sortDirection);
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		try {
			filters = utils.getFilters(filters);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<UnregisteredAthleteDao> athletes = service
				.findDaoPagintation(new UnregisteredAthleteSpecificationConstructor<>(filters), pageable).getContent();
		ByteArrayOutputStream outputStream = excelService.downloadAthletesExcel(columns, athletes, "Atletas");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
	}

}
