package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtilsBean;
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
import com.backend.sprint.model.dto.ColumnDto;
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

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns, HttpServletResponse response) throws IOException {

		List<AthleteDto> athletes = service.findAll();

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		final BeanUtilsBean bub = new BeanUtilsBean();
		data.addAll(athletes.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.parallelStream().map(d -> {
				try {
					return new ExcelValueDto(bub.getProperty(entity, d.getDataField()), CellType.STRING);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Familias", data);
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

	@PostMapping("/relatives")
	public List<AthleteDto> getRelatives(@RequestBody AthleteDto dto) {
		return service.getRelatives(dto);
	}

}
