package com.backend.sprint.controller;

import java.util.ArrayList;
import java.util.List;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.ObservationDto;
import com.backend.sprint.model.dto.SportdataDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.FeeService;
import com.backend.sprint.service.HistoricService;
import com.backend.sprint.service.ObservationService;
import com.backend.sprint.service.SportdataService;
import com.backend.sprint.specifications.SportdataSpecificationConstructor;

@RestController
@RequestMapping("athletes")
public class AthleteController {

	@Autowired
	private AthleteService service;

	@Autowired
	private HistoricService historicService;

	@Autowired
	private ObservationService observationService;

	@Autowired
	private SportdataService sportdataService;

	@Autowired
	private FeeService feeService;

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

	@GetMapping("/{id}/historic")
	public List<HistoricDto> getAthleteHistoric(@PathVariable int id) throws PSQLException {
		return historicService.findAthleteHistoric(id);
	}

	@GetMapping("/{id}/observations")
	public List<ObservationDto> getAthleteObservations(@PathVariable int id) throws PSQLException {
		return observationService.findAthleteObservations(id);
	}

	@PostMapping("/fee")
	public FeeDto getFee(@RequestBody AthleteDto dto) {
		return feeService.getAthleteFee(dto);
	}

	@PostMapping("")
	public AthleteDto save(@RequestBody AthleteDto dto) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setUpdatedById(user.getId());

		return service.save(dto);
	}

	@DeleteMapping("/{id}")
	public AthleteDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

}
