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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.controller.utils.Utils;
import com.backend.sprint.model.dao.ReceiptRegisteredAthleteDao;
import com.backend.sprint.model.dao.ReceiptUnregisteredAthleteDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.model.dto.ReceiptAthleteObservationDto;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.ReceiptAthleteObservationService;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.service.ReceiptRegisteredAthleteService;
import com.backend.sprint.service.ReceiptUnregisteredAthleteService;
import com.backend.sprint.specifications.ReceiptAthleteSpecificationConstructor;
import com.backend.sprint.specifications.ReceiptRegisteredAthleteSpecificationConstructor;
import com.backend.sprint.specifications.ReceiptUnregisteredAthleteSpecificationConstructor;
import com.backend.sprint.utils.ReceiptStatus;

@RestController
@RequestMapping("receipts")
public class ReceiptAthleteController {

	@Autowired
	private ReceiptAthleteService receiptAthleteService;

	@Autowired
	private ReceiptRegisteredAthleteService receiptRegisteredAthleteService;

	@Autowired
	private ReceiptUnregisteredAthleteService receiptUnregisteredAthleteService;

	@Autowired
	private ReceiptAthleteObservationService receiptAthleteObservationService;

	@Autowired
	private ExcelService excelService;

	@Autowired
	private Utils utils;

	@GetMapping("/{id}/athletes")
	public Page<ReceiptAthleteDto> findReceiptIdAthletes(@PathVariable int id,
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {

		try {

			filters = utils.getFilters(filters);
			filters.add("receiptId__=__" + id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}

		return receiptAthleteService.findPagintation(new ReceiptAthleteSpecificationConstructor<>(filters), pageable,
				sort, sortDirection);
	}

	@PostMapping("/athletes")
	public ReceiptAthleteDto save(@RequestBody ReceiptAthleteDto dto) {
		return receiptAthleteService.save(dto);
	}

	@GetMapping("/athletes/{id}")
	public ReceiptAthleteDto findReceiptAthleteById(@PathVariable int id) {
		return receiptAthleteService.findById(id);
	}

	@GetMapping("/athletes/{id}/observations")
	public List<ReceiptAthleteObservationDto> getReceiptAthleteObservations(@PathVariable int id) {
		return receiptAthleteObservationService.findReceiptAthleteObservations(id);
	}

	@GetMapping("/athletes/return/registered")
	public Page<ReceiptAthleteDto> findReturnedReceiptsRegistered(@RequestParam(defaultValue = "season") String season,
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
			filters.add("status__LIKE__" + ReceiptStatus.RETURN.label);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return receiptRegisteredAthleteService.findPagintation(
				new ReceiptRegisteredAthleteSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/athletes/return/unregistered")
	Page<ReceiptAthleteDto> findReturnedReceiptsUnregistered(@RequestParam(defaultValue = "season") String season,
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
			filters.add("season__LIKE__" + season);
			filters.add("status__LIKE__" + ReceiptStatus.RETURN.label);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return receiptUnregisteredAthleteService.findPagintation(
				new ReceiptUnregisteredAthleteSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/athletes/not_paid/registered")
	public Page<ReceiptAthleteDto> findNotPaidReceiptsRegistered(@RequestParam(defaultValue = "season") String season,
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
			filters.add("season__LIKE__" + season);
			filters.add("status__LIKE__" + ReceiptStatus.NOT_PAID.label);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return receiptRegisteredAthleteService.findPagintation(
				new ReceiptRegisteredAthleteSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/athletes/not_paid/unregistered")
	Page<ReceiptAthleteDto> findNotPaidReceiptsUnregistered(@RequestParam(defaultValue = "season") String season,
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
			filters.add("season__LIKE__" + season);
			filters.add("status__LIKE__" + ReceiptStatus.NOT_PAID.label);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return receiptUnregisteredAthleteService.findPagintation(
				new ReceiptUnregisteredAthleteSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@PostMapping("/athletes/return/registered/excel")
	public void returnedReceiptsRegisteredExcel(@RequestParam(defaultValue = "season") String season,
			@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		try {
			filters = utils.getFilters(filters);
			filters.add("season__LIKE__" + season);
			filters.add("status__LIKE__" + ReceiptStatus.RETURN.label);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<ReceiptRegisteredAthleteDao> athletes = receiptRegisteredAthleteService
				.findDaoPagintation(new ReceiptRegisteredAthleteSpecificationConstructor<>(filters), pageable)
				.getContent();
		ByteArrayOutputStream outputStream = excelService.downloadReceiptAthletesExcel(columns, athletes, "Atletas");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());

	}

	@PostMapping("/athletes/return/unregistered/excel")
	public void returnedReceiptsUnregisteredExcel(@RequestParam(defaultValue = "season") String season,
			@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		try {
			filters = utils.getFilters(filters);
			filters.add("season__LIKE__" + season);
			filters.add("status__LIKE__" + ReceiptStatus.RETURN.label);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<ReceiptUnregisteredAthleteDao> athletes = receiptUnregisteredAthleteService
				.findDaoPagintation(new ReceiptUnregisteredAthleteSpecificationConstructor<>(filters), pageable)
				.getContent();
		ByteArrayOutputStream outputStream = excelService.downloadReceiptAthletesExcel(columns, athletes, "Atletas");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());

	}

}
