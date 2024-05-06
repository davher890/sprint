package com.backend.sprint.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.ReceiptDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptAthleteDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.ReceiptRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.service.impl.SportSchoolExternosFeeService;
import com.backend.sprint.service.impl.SportSchoolFuencarralFeeService;
import com.backend.sprint.service.impl.SportSchoolParacuellosFeeService;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.ReceiptStatus;

@Service
@Transactional
public class ReceiptService {

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	@Autowired
	private FeeService feeService;

	@Autowired
	private SportSchoolFuencarralFeeService sportSchoolFuencarralFeeService;

	@Autowired
	private SportSchoolParacuellosFeeService sportSchoolParacuellosFeeService;

	@Autowired
	private SportSchoolExternosFeeService sportSchoolExternosFeeService;

	@Autowired
	private ReceiptAthleteService receiptAthleteService;

	@Autowired
	private ReceiptRepository repository;

	@Autowired
	private ReceiptAthleteRepository receiptAthleteRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public Page<ReceiptDto> findPagintation(Specification<ReceiptDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<ReceiptDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ReceiptDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).filter(d -> d.getName() != null)
				.map(this::convertToDto).sorted(Comparator.comparing(ReceiptDto::getName)).collect(Collectors.toList());
	}

	public ReceiptDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public void deleteByAthleteIds(long id, List<Long> athleteIds) {
		receiptAthleteRepository.deleteByAthleteIds(id, athleteIds);
	}

	public ReceiptDto deleteById(long id) {
		ReceiptDto receipt = this.findById(id);
		receipt.setActive(false);

		receipt = this.save(receipt);

		receiptAthleteRepository.deleteByReceiptId(receipt.getId());

		return receipt;
	}

	public ReceiptDto save(ReceiptDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private ReceiptDto convertToDto(ReceiptDao dao) {
		if (dao == null) {
			return null;
		}
		ReceiptDto dto = new ModelMapper().map(dao, ReceiptDto.class);
		if (dao.getSportSchool() != null) {
			dto.setSportSchoolId(dao.getSportSchool().getId());
			dto.setSportSchoolName(dao.getSportSchool().getName());
		}
		dto.setEnrollmentFee(repository.getSumEnrollmentFee(dto.getId()));
		dto.setMembershipFee(repository.getSumMembershipFee(dto.getId()));
		dto.setMonthlyFee(repository.getSumMonthlyFee(dto.getId()));
		dto.setSportSchoolAnnualFee(repository.getSumSportSchoolAnnualFee(dto.getId()));
		dto.setLicenseFee(repository.getSumLicenseFee(dto.getId()));
		if (dao.getLicenseTypeStr() != null) {
			dto.setLicenseTypes(Arrays.asList(dao.getLicenseTypeStr().split(",")));
		}
		return dto;
	}

	private ReceiptDao convertToDao(ReceiptDto dto) {

		ReceiptDao dao = new ModelMapper().map(dto, ReceiptDao.class);

		if (dto.getSportSchoolId() > 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		} else {
			dao.setSportSchool(null);
		}
		if (dto.getLicenseTypes() != null && !dto.getLicenseTypes().isEmpty()) {
			dao.setLicenseTypeStr(String.join(",", dto.getLicenseTypes()));
		}
		return dao;
	}

	private SportSchoolFeeService getSportSchoolFeeService(long sportSchoolId) {
		switch (Long.valueOf(sportSchoolId).intValue()) {
		case 1: {
			return sportSchoolFuencarralFeeService;
		}
		case 2: {
			return sportSchoolParacuellosFeeService;
		}
		case 3: {
			return sportSchoolExternosFeeService;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + sportSchoolId);
		}
	}

	public ReceiptDto calculateLicenseFee(ReceiptDto dto) {

		dto.setStatus(ReceiptStatus.PROCESSING.name());
		final ReceiptDto receiptDto = save(dto);

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<AthleteDto> registeredAthletes = registeredAthleteService.findBySportSchoolIdAndLicense(
						dto.getSportSchoolId(), dto.getPaymentType(), dto.getLicenseTypes());

				registeredAthletes.parallelStream().forEach(athlete -> {

					ReceiptAthleteDto raDto = new ReceiptAthleteDto();
					raDto.setDays(athlete.getDays());
					raDto.setNumDays(athlete.getNumDays());
					raDto.setAthleteId(athlete.getId());
					raDto.setReceiptId(receiptDto.getId());
					raDto.setMonthlyFee(0);
					raDto.setEnrollmentFee(0);
					raDto.setMembershipFee(0);
					raDto.setLicenseNationalFee(
							dto.getLicenseTypes().contains("national") ? athlete.getLicenseNationalFee() : 0);
					raDto.setLicenseTerritorialFee(
							dto.getLicenseTypes().contains("territorial") ? athlete.getLicenseTerritorialFee() : 0);
					raDto.setStatus(ReceiptStatus.PENDING.name());

					receiptAthleteService.save(raDto);
				});
				receiptDto.setStatus(ReceiptStatus.PENDING.name());
				save(receiptDto);
			}
		}).start();

		return receiptDto;
	}

	public ReceiptDto calculateAnnualFee(ReceiptDto dto) {

		dto.setStatus(ReceiptStatus.PROCESSING.name());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyy-MM-dd");
		Calendar creationDate = Calendar.getInstance();
		Calendar startDate = Calendar.getInstance();
		String season = dto.getSeason();
		try {
			creationDate.setTime(sdf.parse(season.split("_")[1] + "-07-01"));
			startDate.setTime(sdf.parse(season.split("_")[0] + "-08-01"));

			dto.setStartDate(startDate.getTime());
			dto.setEndDate(creationDate.getTime());

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		final ReceiptDto receiptDto = save(dto);

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<AthleteDto> registeredAthletes = athleteService.findReceiptAnnualAthletes(dto.getSportSchoolId(),
						dto.getPaymentType());

				FeeDto feeDto = feeService.findBySportSchoolId(dto.getSportSchoolId());
				SportSchoolFeeService sportSchoolFeeService = getSportSchoolFeeService(dto.getSportSchoolId());

				registeredAthletes.parallelStream().forEach(athlete -> {

					athlete.setLastRegisterDate(AthleteUtils.getLastRegisterDate(athlete.getHistoric().stream()
							.filter(d -> Date.from(d.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
									.before(creationDate.getTime()))
							.collect(Collectors.toList())));
					try {
						FeeDto fee = sportSchoolFeeService.getAthleteFee(athlete, receiptDto, feeDto);
						ReceiptAthleteDto raDto = new ReceiptAthleteDto();
						raDto.setDays(athlete.getDays());
						raDto.setNumDays(athlete.getNumDays());
						raDto.setAthleteId(athlete.getId());
						raDto.setReceiptId(receiptDto.getId());
						raDto.setMonthlyFee(0);
						raDto.setEnrollmentFee((int) fee.getEnrollmentFee());
						raDto.setMembershipFee((int) fee.getMembershipFee());
						raDto.setSportSchoolAnnualFee(0);
						raDto.setLicenseNationalFee(fee.getLicenseNationalFee());
						raDto.setLicenseTerritorialFee(fee.getLicenseTerritorialFee());
						raDto.setLicenseOtherFee(fee.getLicenseOtherFee());
						raDto.setStatus(ReceiptStatus.PENDING.name());

						if (raDto.getMonthlyFee() > 0 || raDto.getEnrollmentFee() > 0 || raDto.getMembershipFee() > 0
								|| raDto.getLicenseNationalFee() > 0 || raDto.getLicenseTerritorialFee() > 0
								|| raDto.getSportSchoolAnnualFee() > 0) {
							receiptAthleteService.save(raDto);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				receiptDto.setStatus(ReceiptStatus.PENDING.name());
				save(receiptDto);
			}
		}).start();
		return receiptDto;
	}

	public ReceiptDto calculateTrimestralFee(ReceiptDto dto) {

		dto.setStatus(ReceiptStatus.PROCESSING.name());
		final ReceiptDto receiptDto = save(dto);

		new Thread(new Runnable() {

			@Override
			public void run() {

				Calendar creationDate = Calendar.getInstance();
				creationDate.setTime(Date.from(dto.getCreationDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

				List<AthleteDto> registeredAthletes = athleteService.findByReceiptTrimestralAthletes(
						dto.getSportSchoolId(), creationDate.getTime(), dto.getStartDate(), dto.getPaymentType());

				FeeDto feeDto = feeService.findBySportSchoolId(dto.getSportSchoolId());
				SportSchoolFeeService sportSchoolFeeService = getSportSchoolFeeService(dto.getSportSchoolId());
				registeredAthletes.parallelStream()
//				.filter(d -> {
//					long[] a = { 487L, 1179L, 3339L };
//					return LongStream.of(a).anyMatch(x -> x == d.getId());
//				})
						.forEach(athlete -> {
							athlete.setLastRegisterDate(
									AthleteUtils.getLastRegisterDate(athlete.getHistoric().stream()
											.filter(d -> Date
													.from(d.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
													.before(creationDate.getTime()))
											.collect(Collectors.toList())));
							try {
								FeeDto fee = sportSchoolFeeService.getAthleteFee(athlete, receiptDto, feeDto);
								ReceiptAthleteDto raDto = new ReceiptAthleteDto();
								raDto.setDays(athlete.getDays());
								raDto.setNumDays(athlete.getNumDays());
								raDto.setAthleteId(athlete.getId());
								raDto.setReceiptId(receiptDto.getId());
								raDto.setMonthlyFee((int) fee.getMonthlyFee());
								raDto.setEnrollmentFee((int) fee.getEnrollmentFee());
								raDto.setMembershipFee((int) fee.getMembershipFee());
								raDto.setSportSchoolAnnualFee((int) fee.getSportSchoolAnnualFee());
								raDto.setLicenseNationalFee(fee.getLicenseNationalFee());
								raDto.setLicenseTerritorialFee(fee.getLicenseTerritorialFee());
								raDto.setStatus(ReceiptStatus.PENDING.name());

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
				save(receiptDto);
			}
		}).start();
		return receiptDto;
	}

}
