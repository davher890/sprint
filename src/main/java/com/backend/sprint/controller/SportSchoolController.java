package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.SelectOptionDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.service.SportSchoolService;
import com.backend.sprint.specifications.SportSchoolSpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("sport_schools")
public class SportSchoolController {

	@Autowired
	private SportSchoolService service;

	@GetMapping("")
	public Page<SportSchoolDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");
		return service.findPagintation(new SportSchoolSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<SportSchoolDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public SportSchoolDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/fee_types")
	public List<SelectOptionDto> findFeeTypesById(@PathVariable int id) {
		return service.findFeeTypesById(id);
	}

	@PostMapping("")
	public SportSchoolDto save(@RequestBody SportSchoolDto dto) {
		return service.save(dto);
	}

	@DeleteMapping("/{id}")
	public SportSchoolDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns, HttpServletResponse response) throws IOException {

		List<SportSchoolDao> sportSchools = service.findAllDao();

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		data.addAll(sportSchools.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
				try {
					return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
							ExcelUtils.getCellType(d));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Escuelas", data);
		IOUtils.copy(bas, response.getOutputStream());
	}
}
