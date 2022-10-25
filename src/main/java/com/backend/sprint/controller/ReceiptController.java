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
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.FeeService;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.service.ReceiptService;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.specifications.ReceiptAthleteSpecificationConstructor;
import com.backend.sprint.specifications.ReceiptSpecificationConstructor;
import com.backend.sprint.utils.PaymentStatus;

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
	private AthleteService athleteService;

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	@Autowired
	private FeeService feeService;

	@Autowired
	private Utils utils;

	@GetMapping("")
	public Page<ReceiptDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, String sort, Direction sortDirection) {
		try {
			filters = utils.getFilters(filters);
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

//	@DeleteMapping("/{id}")
//	public ReceiptDto deleteById(@PathVariable long id) {
//		return service.deleteById(id);
//	}

	@DeleteMapping("/{id}/athletes")
	public void deleteReceiptAthletes(@PathVariable long id,
			@RequestParam(value = "athlete_ids", required = true) List<Long> athleteIds) {
		service.deleteByAthleteIds(id, athleteIds);
	}

	@DeleteMapping("/{id}/athletes/add")
	public void addAthletesToReceipt(@PathVariable long id,
			@RequestParam(value = "athlete_ids", required = true) List<Long> athleteIds) {

		ReceiptDto receipt = service.findById(id);
		athleteIds.stream().map(athleteId -> {

			AthleteDto athlete = athleteService.findById(athleteId);

			FeeDto fee = feeService.getAthleteFee(athlete.getId(), athlete.getFamilyCode(), athlete.getPaymentType(),
					athlete.getSportSchoolId(), athlete.isSpecialization(), athlete.getScheduleIds(),
					athlete.getCategory(), athlete.getLicenseType(), athlete.isEnrollmentFeePaid(),
					receipt.getCollection());

			ReceiptAthleteDto raDto = new ReceiptAthleteDto();
			raDto.setAthleteId(athlete.getId());
			raDto.setReceiptId(receipt.getId());
			raDto.setMonthlyFee(fee.getMonthlyFee());
			raDto.setEnrollmentFee(fee.getEnrollmentFee());
			raDto.setMembershipFee(fee.getMembershipFee());
			raDto.setStatus(PaymentStatus.PENDING.name().toLowerCase());
			return receiptAthleteService.save(raDto);
		}).collect(Collectors.toList());
	}

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

	@PostMapping("/{id}")
	public ReceiptAthleteDto save(@PathVariable int id, @RequestBody ReceiptAthleteDto dto) {
		return receiptAthleteService.save(dto);
	}

	@PostMapping("")
	public ReceiptDto save(@RequestBody ReceiptDto receiptData) throws Exception {

		final ReceiptDto dto = service.save(receiptData);

		// Save athletes to the receipt
		List<AthleteDto> registeredAthletes = registeredAthleteService
				.findBySportSchoolIdAndLastRegisterDate(dto.getSportSchoolId(), dto.getLastRegisterDate());

		registeredAthletes.stream().map(athlete -> {

			FeeDto fee = feeService.getAthleteFee(athlete.getId(), athlete.getFamilyCode(), athlete.getPaymentType(),
					athlete.getSportSchoolId(), athlete.isSpecialization(), athlete.getScheduleIds(),
					athlete.getCategory(), athlete.getLicenseType(), athlete.isEnrollmentFeePaid(),
					dto.getCollection());

			ReceiptAthleteDto raDto = new ReceiptAthleteDto();
			raDto.setAthleteId(athlete.getId());
			raDto.setReceiptId(dto.getId());
			raDto.setMonthlyFee(fee.getMonthlyFee());
			raDto.setEnrollmentFee(fee.getEnrollmentFee());
			raDto.setMembershipFee(fee.getMembershipFee());
			raDto.setStatus(PaymentStatus.PENDING.name().toLowerCase());
			return receiptAthleteService.save(raDto);
		}).collect(Collectors.toList());
		return dto;
	}

	@PostMapping("/{id}/excel")
	public void excel(@PathVariable int id,
			@RequestParam(value = "concept", required = false, defaultValue = "") String concept,
			HttpServletResponse response) throws IOException {

		List<ReceiptAthleteDao> daoList = receiptAthleteService.findDaoByReceiptId(id);
		ByteArrayOutputStream outputStream = excelService.downloadReceiptAthletesExcel(daoList, concept, "Recibo");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
	}
}
