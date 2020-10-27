package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.FamilyService;
import com.backend.sprint.specifications.FamilySpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("families")
public class FamilyController {

	@Autowired
	private FamilyService service;

	@Autowired
	private AthleteService athleteService;

	@GetMapping("")
	public Page<FamilyDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new FamilySpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<FamilyDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/excel")
	public void excel(HttpServletResponse response) throws IOException {

		List<FamilyDto> families = service.findAll();

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().add(new ExcelValueDto("Primer Appellido", CellType.STRING));
		header.getData().add(new ExcelValueDto("Segundo Appellido", CellType.STRING));
		data.add(header);

		data.addAll(families.parallelStream().map(family -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			return dataDto;
		}).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Familias", data);
		IOUtils.copy(bas, response.getOutputStream());
	}

	@GetMapping("/{id}")
	public FamilyDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/athletes")
	public Set<AthleteDto> findAthletesByFAmily(@PathVariable int id) {
		return athleteService.findByFamily(id);
	}

	@PostMapping("")
	public FamilyDto save(@RequestBody FamilyDto dto) {
		return service.save(dto);
	}
}
