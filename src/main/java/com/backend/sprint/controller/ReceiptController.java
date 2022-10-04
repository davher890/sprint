package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.service.ReceiptService;
import com.backend.sprint.specifications.ReceiptAthleteSpecificationConstructor;
import com.backend.sprint.specifications.ReceiptSpecificationConstructor;
import com.backend.sprint.specifications.RegisteredAthleteSpecificationConstructor;
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

	@GetMapping("/{id}")
	public ReceiptDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@DeleteMapping("/{id}")
	public ReceiptDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@GetMapping("/athletes")
	public Page<ReceiptAthleteDto> findReceiptAthletes(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection,
			@RequestParam(defaultValue = "collection") String collection,
			@RequestParam(defaultValue = "sportSchoolId") long sportSchoolId) {

		try {
			filters = utils.getFilters(filters);
			filters.add("sportSchoolId__=__" + sportSchoolId);
			filters.add("paymentType__ins__DOM,TRANSF");
			// filters.add("collection__=__" + collection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}

		return service.findReceiptAthletes(new RegisteredAthleteSpecificationConstructor<>(filters), pageable, sort,
				sortDirection, collection);
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
	public ReceiptDto save(@RequestBody ReceiptDto dto) throws Exception {

		dto = service.save(dto);
		long receiptId = dto.getId();

		List<String> filters = utils.getFilters(null);
		if (dto.getSportSchoolId() > 0) {
			filters.add("sportSchoolId__=__" + dto.getSportSchoolId());
		}
		String collection = dto.getCollection();
		if (collection.equals("september")) {
			filters.add("lastRegisterDate__lte__2022-10-01");
		} else if (collection.equals("october")) {
			filters.add("lastRegisterDate__lte__2023-01-01");
		} else if (collection.equals("january")) {
			filters.add("lastRegisterDate__lte__2023-04-01");
		} else if (collection.equals("april")) {
			filters.add("lastRegisterDate__lte__2023-07-01");
		} else if (collection.equals("july")) {
			filters.add("lastRegisterDate__lte__2023-08-01");
		}
		List<ReceiptAthleteDto> athletes = service
				.findReceiptAthletesList(new RegisteredAthleteSpecificationConstructor<>(filters), collection);
		athletes.parallelStream().forEach(a -> {

			ReceiptAthleteDto raDto = new ReceiptAthleteDto();
			raDto.setAthleteId(a.getId());
			raDto.setReceiptId(receiptId);
			raDto.setMonthlyFee(a.getMonthlyFee());
			raDto.setEnrollmentFee(a.getEnrollmentFee());
			raDto.setMembershipFee(a.getMembershipFee());
			raDto.setStatus(PaymentStatus.PENDING.name().toLowerCase());
			receiptAthleteService.save(raDto);
		});
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
