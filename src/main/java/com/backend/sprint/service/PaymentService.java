package com.backend.sprint.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.AthleteDto;

@Service
public class PaymentService {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private FeeService feeService;

	public List<AthleteDto> septemberAthletes(int sportSchoolId) throws ParseException {

		// All athletes membership fee
		List<AthleteDto> athletes = athleteService.findAllRegistered().parallelStream().map(d -> {
			d.setPaymentAmount(feeService.getMembershipFee(d));
			return d;
		}).collect(Collectors.toList());

		// New Athletes enrollment fee
		Date startDate = dateFormat.parse("2021-08-01");
		Date endDate = dateFormat.parse("2021-10-01");
		athletes.addAll(athleteService.findByRegistrationDates(startDate, endDate).parallelStream().map(d -> {
			d.setPaymentAmount(feeService.getEnrollmentFee(d));
			return d;
		}).collect(Collectors.toList()));

		// September month fee
		startDate = dateFormat.parse("2021-08-01");
		endDate = dateFormat.parse("2021-09-25");
		athletes.addAll(athleteService.findByRegistrationDates(startDate, endDate).parallelStream().map(d -> {
			d.setPaymentAmount(feeService.getMonthlyFee(d));
			return d;
		}).collect(Collectors.toList()));

		athletes = athletes.stream().collect(Collectors.groupingBy(AthleteDto::getId)).entrySet().stream()
				.map(e -> e.getValue().stream().reduce((f1, f2) -> {
					f2.setPaymentAmount(f2.getPaymentAmount() + f1.getPaymentAmount());
					return f1;
				})).map(f -> f.get()).collect(Collectors.toList());
		return athletes;

	}

}
