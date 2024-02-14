package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.controller.utils.Utils;
import com.backend.sprint.model.dao.ReceiptAthleteDao;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.service.ReceiptService;
import com.backend.sprint.specifications.ReceiptSpecificationConstructor;
import com.backend.sprint.utils.PaymentType;
import com.backend.sprint.utils.ReceiptStatus;

@RestController
@RequestMapping("receipts")
public class ReceiptController {

	@Autowired
	private ReceiptService service;

	@Autowired
	private ExcelService excelService;

	@Autowired
	private ReceiptAthleteService receiptAthleteService;

	@Autowired
	private Utils utils;

	@GetMapping("")
	public Page<ReceiptDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, String sort, Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
			filters.add("active__==__true");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
		return service.findPagintation(new ReceiptSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/all")
	public List<ReceiptDto> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ReceiptDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@DeleteMapping("/{id}")
	public ReceiptDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@DeleteMapping("/{id}/athletes")
	public void deleteReceiptAthletes(@PathVariable long id,
			@RequestParam(value = "athlete_ids", required = true) List<Long> athleteIds) {
		service.deleteByAthleteIds(id, athleteIds);
	}

	@GetMapping("/{id}/validate")
	public ReceiptDto receiptValidation(@PathVariable int id) {

		ReceiptDto dto = service.findById(id);
		dto.setStatus(ReceiptStatus.VALIDATED.name());
		dto = service.save(dto);
		if (dto.getPaymentType().equals(PaymentType.DomiciliaciónBancaria.label)) {
			receiptAthleteService.paidReceiptId(id);
		}
		if (dto.getPaymentType().equals(PaymentType.EfectivoYTransferencia.label)) {
			receiptAthleteService.notPaidReceiptId(id);
		}
		return dto;
	}

	@PostMapping("")
	public ReceiptDto save(@RequestBody ReceiptDto receiptData) throws Exception {

		ReceiptDto dto = service.save(receiptData);

		if (receiptData.getId() > 0) {
			receiptAthleteService.deleteByReceiptId(receiptData.getId());
		}

		if (dto.getReceiptType() != null && dto.getReceiptType().equals("LICENSE")) {
			dto = service.calculateLicenseFee(dto);
		} else if (dto.getReceiptType() != null && dto.getReceiptType().equals("ANNUAL")) {
			dto = service.calculateAnnualFee(dto);
		} else {
			dto = service.calculateTrimestralFee(dto);
		}
		return dto;

	}

	@PostMapping("/{id}/excel")
	public void excel(@PathVariable int id,
			@RequestParam(value = "concept", required = false, defaultValue = "") String concept,
			HttpServletResponse response) throws IOException {

		List<ReceiptAthleteDao> daoList = receiptAthleteService.findDaoByReceiptId(id).stream()
				.collect(Collectors.toList());
		ByteArrayOutputStream outputStream = excelService.downloadReceiptAthletesExcel(daoList, concept, "Recibo");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
	}

}
