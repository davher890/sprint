package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.HistoricService;
import com.backend.sprint.specifications.AthleteSpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;
import com.backend.sprint.utils.HistoricType;

@RestController
@RequestMapping("athletes")
public class AthleteController {

	@Autowired
	private AthleteService service;

	@Autowired
	private HistoricService historicService;

	@GetMapping("")
	public Page<AthleteDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("register__-__-");
		return service.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/historic")
	public Page<AthleteDto> findHistoricPagintation(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("unregister__-__-");
		return service.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<AthleteDto> findAll() {
		return service.findAll();
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns, HttpServletResponse response) throws IOException {

		List<AthleteDao> athletes = service.findAllExcel();

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

	@PostMapping("/{id}/register")
	public HistoricDto registerAthlete(@PathVariable int id, @RequestBody HistoricDto historic) throws PSQLException {
		historic.setAthleteId(id);
		historic.setType(HistoricType.REGISTER.name());
		return historicService.save(historic);
	}

	@PostMapping("/{id}/unregister")
	public HistoricDto unregisterAthlete(@PathVariable int id, @RequestBody HistoricDto historic) throws PSQLException {
		historic.setAthleteId(id);
		historic.setType(HistoricType.UNREGISTER.name());
		return historicService.save(historic);
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
		try {
			return service.save(dto);
		} catch (DataIntegrityViolationException e) {

			dto.setErrorMessage("Atleta ya existente.");
			return dto;
		}
	}

	@PostMapping("/relatives")
	public List<AthleteDto> getRelatives(@RequestBody AthleteDto dto) {
		return service.getRelatives(dto);
	}

}
