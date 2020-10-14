package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.specifications.AthleteSpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("athletes")
public class AthleteController {

	@Autowired
	private AthleteService service;

	private SimpleDateFormat birthDateFormat = new SimpleDateFormat("YYYY-MM-dd");

	@GetMapping("")
	public Page<AthleteDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<AthleteDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/excel")
	public void excel(HttpServletResponse response) throws IOException {

		List<AthleteDto> athletes = service.findAll();

		List<ExcelDataDto> data = athletes.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().add(new ExcelValueDto(entity.getName(), CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(birthDateFormat.format(entity.getBirthDate()), CellType.STRING));
			return dataDto;
		}).collect(Collectors.toList());

		List<String> headers = new ArrayList<String>();
		headers.add("Nombre");
		headers.add("Fecha de nacimiento");

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Familias", headers, data);
		IOUtils.copy(bas, response.getOutputStream());
	}

	@GetMapping("/{id}")
	public AthleteDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/groups")
	public List<GroupDto> findGroupsById(@PathVariable int id) {
		return service.findGroupsById(id);
	}

	@PostMapping("/fee")
	public FeeDto getFee(@RequestBody AthleteDto dto) {
		return service.getFee(dto);
	}

	@PostMapping("")
	public AthleteDto save(@RequestBody AthleteDto dto) {
		return service.save(dto);
	}

}
