package com.backend.sprint.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.FeeRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.utils.FeeTypeEnum;
import com.backend.sprint.utils.PaymentType;

@Service
@Transactional
public class FeeService {

//	@Autowired
//	private LicenseTypeCategoryFeeRepository licenseTypeCategoryFeeRepository;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private FeeRepository repository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public FeeDto save(FeeDto dto) {

		FeeDao oldFee = repository.findBySportSchoolId(dto.getSportSchoolId());

		if (oldFee != null) {
			FeeDto oldDto = findById(oldFee.getId());

			oldDto.setLicenseNationalFee(dto.getLicenseNationalFee());
			oldDto.setLicenseTerritorialFee(dto.getLicenseTerritorialFee());

			oldDto.setEnrollmentFee(dto.getEnrollmentFee());
			oldDto.setMembershipFee(dto.getMembershipFee());
			oldDto.setMonthlyFee(dto.getMonthlyFee());
			// Quarterly
			oldDto.setQuarterOneDay(dto.getQuarterOneDay());
			oldDto.setQuarterTwoDays(dto.getQuarterTwoDays());
			oldDto.setQuarterThreeDays(dto.getQuarterThreeDays());
			oldDto.setQuarterSpecialization(dto.getQuarterSpecialization());

			oldDto.setUpdatedAt(null);
			return convertToDto(repository.save(convertToDao(oldDto)));
		} else {
			return convertToDto(repository.save(convertToDao(dto)));
		}
	}

	public FeeDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public FeeDto findBySportSchoolId(long sportSchoolId) {
		return convertToDto(repository.findBySportSchoolId(sportSchoolId));
	}

	public FeeDto getAthleteFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee)
			throws ParseException {

		FeeDto fee = new FeeDto();
		float monthlyFee = this.getMonthlyFee(athlete, receiptDto, sportSchoolFee);
		fee.setMonthlyFee(monthlyFee);

		float enrollmentFee = this.getEnrollmentFee(athlete.getId(), athlete.getFamiliarNumber(),
				athlete.isEnrollmentFeePaid(), sportSchoolFee);
		fee.setEnrollmentFee(enrollmentFee);

		float membershipFee = this.getMembershipFee(athlete.getId(), athlete.getPaymentType(), athlete.getFeeType(),
				athlete.getFamilyCode(), athlete.isMembershipFeePaid(), sportSchoolFee);
		fee.setMembershipFee(membershipFee);

		if (receiptDto.getReceiptType() != null && receiptDto.getReceiptType().equals("LICENSE")) {
			fee.setLicenseNationalFee(athlete.getLicenseNationalFee());
			fee.setLicenseTerritorialFee(athlete.getLicenseNationalFee());
		}

		return fee;
	}

	public FeeDto convertToDto(FeeDao dao) {
		if (dao == null) {
			return new FeeDto();
		}
		FeeDto dto = new ModelMapper().map(dao, FeeDto.class);
		SportSchoolDao sportSchool = dao.getSportSchool();
		if (sportSchool != null) {
			dto.setSportSchoolId(sportSchool.getId());
		}

		return dto;
	}

	public FeeDao convertToDao(FeeDto dto) {
		if (dto == null) {
			return null;
		}

		FeeDao dao = new ModelMapper().map(dto, FeeDao.class);
		long sportSchoolId = dto.getSportSchoolId();
		if (sportSchoolId != 0) {
			Optional<SportSchoolDao> sportSchool = sportSchoolRepository.findById(sportSchoolId);
			dao.setSportSchool(sportSchool.get());
		}
		return dao;
	}

	// Annually
	public float getMembershipFee(long id, String paymentType, String feeType, long familyCode,
			boolean isMembershipFeePaid, FeeDto sportSchoolFee) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label) && !feeType.equals(FeeTypeEnum.CLUB_ANUAL.name())
				&& !isMembershipFeePaid) {

			fee = sportSchoolFee.getMembershipFee();
			FamilyDto family = familyService.findByCode(familyCode);
			if (family != null) {
				int familyCount = family.getCount();
				if (familyCount > 0) {

					// Already registered athlete
					if (id > 0) {
						if (familyCount >= 3) {
							long min = family.getAthleteIds().stream().mapToLong(v -> v).min().getAsLong();
							if (min != id) {
								fee = 0;
							}
						}
					}
					// New athlete
					if (id == 0) {
						if (familyCount > 2) {
							fee /= (familyCount + 1);
						}
					}
				}
			}
		}
		return fee;
	}

	// Once
	public float getEnrollmentFee(long id, long familiarNumber, boolean isEnrollmentFeePaid, FeeDto sportSchoolFee) {

		float fee = 0;
		if (id == 0 || !isEnrollmentFeePaid) {
			fee = sportSchoolFee.getEnrollmentFee();
			if (sportSchoolFee.getSportSchoolId() == 3) {
				fee = 10;
			} else {
				if (familiarNumber > 1) {
					fee *= 0.5;
				}
			}

		}
		return fee;

	}

	public float getMonthlyFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee) throws ParseException {

		float trimestralFee = 0;

//		String licenseType = athlete.getLicenseType();
//		String category = athlete.getCategory();

		if (!athlete.getPaymentType().equals(PaymentType.Subvencionado.label)) {

//			if (sportSchoolFee.getSportSchoolId() == 1 || sportSchoolFee.getSportSchoolId() == 2) {
			if (athlete.getFeeType() != null && (athlete.getFeeType().equals(FeeTypeEnum.CLUB.name())
					|| athlete.getFeeType().equals(FeeTypeEnum.CLUB_ANUAL.name()))) {
				if (athlete.isSpecialization()) {
					trimestralFee = sportSchoolFee.getQuarterSpecialization();
				} else {
					int numDays = athlete.getNumDays() == null ? 0 : athlete.getNumDays();
					if (numDays > 0) {
						switch (numDays) {
						case 1:
							trimestralFee = sportSchoolFee.getQuarterOneDay();
							break;
						case 2:
							trimestralFee = sportSchoolFee.getQuarterTwoDays();
							break;
						default:
							trimestralFee = sportSchoolFee.getQuarterThreeDays();
							break;
						}
					}
//					else {
//						if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
//							LicenseTypeCategoryFeeDao licenseTypeCategoryFee = licenseTypeCategoryFeeRepository
//									.findByLicenseTypeAndCategory(licenseType, category);
//							trimestralFee = licenseTypeCategoryFee.getFee();
//						}
//					}
				}
				if (athlete.getFeeType() != null && athlete.getFeeType().equals(FeeTypeEnum.CLUB_ANUAL.name())) {
					trimestralFee *= (10. / 3);
				} else {

					int diffMonths = getPaymentMonths(receiptDto.getStartDate(), receiptDto.getEndDate(),
							athlete.getLastRegisterDate());
					trimestralFee *= (float) diffMonths / 3;
				}
			}
//		if (sportSchoolFee.getSportSchoolId() == 3 || sportSchoolFee.getSportSchoolId() == 5) {
//			if (athlete.getFeeType().equals(FeeTypeEnum.FED.name())) {
//				if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
//					LicenseTypeCategoryFeeDao licenseTypeCategoryFee = licenseTypeCategoryFeeRepository
//							.findByLicenseTypeAndCategory(licenseType, category);
//					trimestralFee = licenseTypeCategoryFee.getFee();
//				}
//			}

		}
		return trimestralFee;

	}

	private int getPaymentMonths(Date startDate, Date endDate, LocalDate registerDate) {

		int numMonths = 0;
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(startDate);

		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(endDate);

		Calendar registerDateCalendar = Calendar.getInstance();
		registerDateCalendar.setTime(Date.from(registerDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

		while (startDateCalendar.before(endDateCalendar)) {
			if (startDateCalendar.get(Calendar.MONTH) == registerDateCalendar.get(Calendar.MONTH)
					&& startDateCalendar.get(Calendar.YEAR) == registerDateCalendar.get(Calendar.YEAR)) {
				if (registerDateCalendar.get(Calendar.DAY_OF_MONTH) < 25) {
					numMonths++;
				}
			} else {
				numMonths++;
			}

			startDateCalendar.add(Calendar.MONTH, 1);
		}
		return numMonths;
	}

}
