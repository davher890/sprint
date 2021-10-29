package com.backend.sprint.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.SportdataDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.FeeService;
import com.backend.sprint.service.HistoricService;
import com.backend.sprint.service.SportdataService;
import com.backend.sprint.specifications.AthleteSpecificationConstructor;
import com.backend.sprint.specifications.SportdataSpecificationConstructor;
import com.backend.sprint.utils.HistoricType;

@RestController
@RequestMapping("athletes")
public class AthleteController {

	@Autowired
	private AthleteService service;

	@Autowired
	private HistoricService historicService;

	@Autowired
	private SportdataService sportdataService;

	@Autowired
	private FeeService feeService;

	@Autowired
	private ExcelService excelService;

	@GetMapping("")
	public Page<AthleteDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add(HistoricType.REGISTER.name() + "__-__-");
		return service.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add(HistoricType.REGISTER.name() + "__-__-");
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<AthleteDao> athletes = service.findDaoPagintation(new AthleteSpecificationConstructor<>(filters), pageable)
				.getContent();
		excelService.downloadAthletesExcel(columns, response, athletes, "Atletas");

	}

	@GetMapping("/historic")
	public Page<AthleteDto> findHistoricPagintation(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add(HistoricType.UNREGISTER.name() + "__-__-");
		return service.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
	}

	@PostMapping("/historic/excel")
	public void historicExcel(@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add(HistoricType.UNREGISTER.name() + "__-__-");
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<AthleteDao> athletes = service.findDaoPagintation(new AthleteSpecificationConstructor<>(filters), pageable)
				.getContent();
		excelService.downloadAthletesExcel(columns, response, athletes, "Historico");
	}

	@GetMapping("/all")
	public List<AthleteDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/phone")
	public List<AthleteDto> findByPhoneList(
			@RequestParam(value = "phone_list", required = true) List<String> phoneList) {
		return service.findByPhoneList(phoneList);
	}

	@GetMapping("/surnames")
	public List<AthleteDto> findBySurnames(@RequestParam(value = "first_surname", required = true) String firstSurname,
			@RequestParam(value = "second_surname", required = true) String secondSurname) {
		return service.findBySurnames(firstSurname, secondSurname);
	}

	@GetMapping("/{id}")
	public AthleteDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/sportdata")
	public Page<SportdataDto> athleteSportdata(@PathVariable int id, Pageable pageable) {
		List<String> filters = new ArrayList<>();
		filters.add("athleteId__=__" + id);
		return sportdataService.findPagintation(new SportdataSpecificationConstructor<>(filters), pageable);
	}

	@PostMapping("/{id}/register")
	public HistoricDto registerAthlete(@PathVariable int id, @RequestBody HistoricDto historic) throws PSQLException {
		return historicService.register(id, historic);
	}

	@PostMapping("/{id}/unregister")
	public HistoricDto unregisterAthlete(@PathVariable int id, @RequestBody HistoricDto historic) throws PSQLException {
		return historicService.unregister(id, historic);
	}

	@PostMapping("/fee")
	public FeeDto getFee(@RequestBody AthleteDto dto) {
		return feeService.getAthleteFee(dto);
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

}
