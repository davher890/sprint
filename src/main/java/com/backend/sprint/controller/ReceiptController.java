package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
import com.backend.sprint.service.FeeTypeService;
import com.backend.sprint.service.ReceiptAthleteService;
import com.backend.sprint.service.ReceiptService;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.specifications.ReceiptSpecificationConstructor;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.FeeTypeEnum;
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
	private AthleteService athleteService;

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	@Autowired
	private FeeService feeService;

	@Autowired
	private FeeTypeService feeTypeService;

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

//	@DeleteMapping("/{id}/athletes/add")
//	public void addAthletesToReceipt(@PathVariable long id,
//			@RequestParam(value = "athlete_ids", required = true) List<Long> athleteIds) {
//
//		ReceiptDto receipt = service.findById(id);
//		athleteIds.stream().map(athleteId -> {
//
//			AthleteDto athlete = athleteService.findById(athleteId);
//			FeeDto feeDto = feeService.findBySportSchoolId(athlete.getSportSchoolId());
//
//			int numDays = athlete.getScheduleIds() == null ? 0 : athlete.getScheduleIds().size();
//			FeeDto fee = feeService.getAthleteFee(athlete, numDays, feeDto);
//
//			ReceiptAthleteDto raDto = new ReceiptAthleteDto();
//			raDto.setAthleteId(athlete.getId());
//			raDto.setReceiptId(receipt.getId());
//			raDto.setMonthlyFee((int) fee.getMonthlyFee());
//			raDto.setEnrollmentFee((int) fee.getEnrollmentFee());
//			raDto.setMembershipFee((int) fee.getMembershipFee());
//			raDto.setStatus(PaymentStatus.PENDING.name().toLowerCase());
//			return receiptAthleteService.save(raDto);
//		}).collect(Collectors.toList());
//	}

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
			dto = calculateLicenseFee(dto);
		} else {
			dto = calculateTrimestralFee(dto);
		}
		return dto;
	}

	private ReceiptDto calculateLicenseFee(ReceiptDto dto) {

		dto.setStatus(ReceiptStatus.PROCESSING.name());
		final ReceiptDto receiptDto = service.save(dto);

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<AthleteDto> registeredAthletes = registeredAthleteService.findBySportSchoolIdAndLicense(
						dto.getSportSchoolId(), dto.getPaymentType(), dto.getLicenseTypes());

				registeredAthletes.parallelStream()
//					.filter(a -> a.getId() == 856 || a.getId() == 995)
						.forEach(athlete -> {

							ReceiptAthleteDto raDto = new ReceiptAthleteDto();
							raDto.setDays(athlete.getDays());
							raDto.setNumDays(athlete.getNumDays());
							raDto.setLastRegisterDate(athlete.getLastRegisterDate());
							raDto.setLastUnregisterDate(athlete.getLastUnregisterDate());
							raDto.setAthleteId(athlete.getId());
							raDto.setReceiptId(receiptDto.getId());
							raDto.setMonthlyFee(0);
							raDto.setEnrollmentFee(0);
							raDto.setMembershipFee(0);
							raDto.setLicenseNationalFee(
									dto.getLicenseTypes().contains("national") ? athlete.getLicenseNationalFee() : 0);
							raDto.setLicenseTerritorialFee(
									dto.getLicenseTypes().contains("territorial") ? athlete.getLicenseTerritorialFee()
											: 0);
							raDto.setStatus(ReceiptStatus.PENDING.name());

							receiptAthleteService.save(raDto);
						});
				receiptDto.setStatus(ReceiptStatus.PENDING.name());
				service.save(receiptDto);
			}
		}).start();

		return receiptDto;
	}

	private ReceiptDto calculateTrimestralFee(ReceiptDto dto) {

		dto.setStatus(ReceiptStatus.PROCESSING.name());
		final ReceiptDto receiptDto = service.save(dto);

		new Thread(new Runnable() {

			@Override
			public void run() {

				Calendar creationDate = Calendar.getInstance();
				creationDate.setTime(Date.from(dto.getCreationDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

				// Save athletes to the receipt
				List<AthleteDto> registeredAthletes = athleteService.findBySportSchoolIdAndLastRegisterDate(
						dto.getSportSchoolId(), creationDate.getTime(), dto.getStartDate(), dto.getPaymentType(),
						Arrays.asList(new Long[] { FeeTypeEnum.CLUB.id, FeeTypeEnum.CLUB_ANUAL.id }));

				FeeDto feeDto = feeService.findBySportSchoolId(dto.getSportSchoolId());
				registeredAthletes.parallelStream()
//					.filter(a -> a.getId() == 856 || a.getId() == 995)
						.forEach(athlete -> {

							athlete.setLastRegisterDate(
									AthleteUtils.getLastRegisterDate(athlete.getHistoric().stream()
											.filter(d -> Date
													.from(d.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
													.before(creationDate.getTime()))
											.collect(Collectors.toList())));

							try {
								FeeDto fee = feeService.getAthleteFee(athlete, receiptDto, feeDto);
								ReceiptAthleteDto raDto = new ReceiptAthleteDto();
								raDto.setDays(athlete.getDays());
								raDto.setNumDays(athlete.getNumDays());
								raDto.setLastRegisterDate(athlete.getLastRegisterDate());
								raDto.setLastUnregisterDate(athlete.getLastUnregisterDate());
								raDto.setAthleteId(athlete.getId());
								raDto.setReceiptId(receiptDto.getId());
								raDto.setMonthlyFee((int) fee.getMonthlyFee());
								raDto.setEnrollmentFee((int) fee.getEnrollmentFee());
								raDto.setMembershipFee((int) fee.getMembershipFee());
								raDto.setSportSchoolAnnualFee((int) fee.getSportSchoolAnnualFee());
								raDto.setLicenseNationalFee(fee.getLicenseNationalFee());
								raDto.setLicenseTerritorialFee(fee.getLicenseTerritorialFee());
								raDto.setStatus(ReceiptStatus.PENDING.name());

								// Look for season receipts.
								Set<ReceiptAthleteDto> receipts = receiptAthleteService
										.findByAthleteIdAndReceiptStatusAndSeason(athlete.getId(),
												ReceiptStatus.VALIDATED.name(), receiptDto.getSeason());
								if (receipts != null && !receipts.isEmpty()) {
									// There are more previous receipts.
									if (athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB.label)) {

										// Season Trimestral receipt already paid.
										boolean paidReceipt = receipts.stream()
												.anyMatch((ReceiptAthleteDto d) -> d.getStartDate()
														.compareTo(receiptDto.getStartDate()) == 0
														&& d.getEndDate().compareTo(receiptDto.getEndDate()) == 0
														&& d.getStatus().equals(ReceiptStatus.PAID.name()));
										if (paidReceipt) {
											raDto.setMonthlyFee(0);
										} else {
											// Pending receipts from the same season.
											boolean pendingReceipts = receipts.stream()
													.anyMatch((ReceiptAthleteDto d) -> d.getStatus()
															.equals(ReceiptStatus.RETURN.name()));
											// Remove athlete
											if (pendingReceipts) {
												raDto.setMonthlyFee(0);
												raDto.setEnrollmentFee(0);
												raDto.setMembershipFee(0);
											} else {
												boolean errorReceipts = receipts.stream()
														.anyMatch((ReceiptAthleteDto d) -> d.getStatus()
																.equals(ReceiptStatus.SENT_BY_ERROR.name()));
												if (errorReceipts && !athlete.isRegistered()) {
													raDto.setMonthlyFee(0);
													raDto.setEnrollmentFee(0);
													raDto.setMembershipFee(0);
												}
											}
										}
									} else if (athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB_ANUAL.label)) {
										// Annual fee already paid
										if (receipts.stream().anyMatch((ReceiptAthleteDto d) -> d.getStatus()
												.equals(ReceiptStatus.PAID.name()))) {
											raDto.setMonthlyFee(0);
											raDto.setEnrollmentFee(0);
											raDto.setMembershipFee(0);
										}
									}
								}

								if (raDto.getMonthlyFee() > 0 || raDto.getEnrollmentFee() > 0
										|| raDto.getMembershipFee() > 0 || raDto.getSportSchoolAnnualFee() > 0) {
									receiptAthleteService.save(raDto);
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
				receiptDto.setStatus(ReceiptStatus.PENDING.name());
				service.save(receiptDto);
			}
		}).start();
		return receiptDto;
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
