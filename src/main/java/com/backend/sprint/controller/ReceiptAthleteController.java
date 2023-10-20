package com.backend.sprint.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.specifications.ReceiptAthleteSpecificationConstructor;

@RestController
@RequestMapping("receipts")
public class ReceiptAthleteController {

	@Autowired
	private ReceiptAthleteService receiptAthleteService;

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

	@GetMapping("/return")
	public Page<ReceiptAthleteDto> findReturnedReceipts(Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {

		try {
			List<String> filters = utils.getFilters(null);
			filters.add("status__=__return");

			return receiptAthleteService.findPagintation(new ReceiptAthleteSpecificationConstructor<>(filters),
					pageable, sort, sortDirection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Page.empty();
		}
	}
}
