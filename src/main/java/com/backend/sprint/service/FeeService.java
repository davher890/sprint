package com.backend.sprint.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeDao;
import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.FeeRepository;
import com.backend.sprint.repository.RegisteredAthleteRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.utils.FeeTypeEnum;
import com.backend.sprint.utils.MembershipTypeEnum;
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
	private RegisteredAthleteRepository registeredAthleteRepository;

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

			oldDto.setTrackFee(dto.getTrackFee());

			oldDto.setSportSchoolAnnualFee(dto.getSportSchoolAnnualFee());
			oldDto.setEquipmentAnnualFee(dto.getEquipmentAnnualFee());

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

		FamilyDto family = familyService.findByCode(athlete.getFamilyCode());
		List<RegisteredAthleteDao> familyAthltes = null;

		if (family != null) {
			familyAthltes = registeredAthleteRepository.findByAthleteIds(new ArrayList<Long>(family.getAthleteIds()));
		}

		FeeDto fee = new FeeDto();
		float monthlyFee = this.getMonthlyFee(athlete, receiptDto, sportSchoolFee);
		fee.setMonthlyFee(monthlyFee);

		float enrollmentFee = this.getEnrollmentFee(athlete.getId(), familyAthltes, athlete.isEnrollmentFeePaid(),
				sportSchoolFee, athlete.getMembershipType());
		fee.setEnrollmentFee(enrollmentFee);

		float membershipFee = this.getMembershipFee(athlete.getId(), athlete.getPaymentType(), athlete.getFeeTypeName(),
				familyAthltes, athlete.isMembershipFeePaid(), sportSchoolFee);
		fee.setMembershipFee(membershipFee);

		float nationalLicenseFee = this.getLicenseFee(sportSchoolFee.getSportSchoolId(), athlete.getFeeTypeId(),
				athlete.getMembershipType(), receiptDto.getReceiptType(), athlete.getLicenseNationalFee());
		fee.setLicenseNationalFee(nationalLicenseFee);

		// Externos
		float territorialLicenseFee = this.getLicenseFee(sportSchoolFee.getSportSchoolId(), athlete.getFeeTypeId(),
				athlete.getMembershipType(), receiptDto.getReceiptType(), athlete.getLicenseTerritorialFee());
		fee.setLicenseTerritorialFee(territorialLicenseFee);

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

	private float getLicenseFee(long sportSchoolId, long feeTypeId, String membershipType, String receiptType,
			float territorialLicenseFee) {
		float fee = 0;

		if (receiptType != null && receiptType.equals("LICENSE")) {
			fee = territorialLicenseFee;
		}
		if (sportSchoolId == 3) {
			if (feeTypeId == FeeTypeEnum.FED.id || (feeTypeId == FeeTypeEnum.SOCIO.id && membershipType != null
					&& membershipType.equals(MembershipTypeEnum.ATHLETE.name()))) {
				fee = territorialLicenseFee;
			}
		}
		return fee;

	}

	// Annually
	private float getMembershipFee(long id, String paymentType, String feeType,
			List<RegisteredAthleteDao> familyAthltes, boolean isMembershipFeePaid, FeeDto sportSchoolFee) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label) && !feeType.equals(FeeTypeEnum.CLUB_ANUAL.label)
				&& !isMembershipFeePaid) {

			fee = sportSchoolFee.getMembershipFee();
			if (familyAthltes != null) {

				int familyCount = familyAthltes.size();
				if (familyCount > 0) {

					// Already registered athlete
					if (id > 0) {
						if (familyCount >= 3) {
							long min = familyAthltes.stream().mapToLong(v -> v.getId()).min().getAsLong();
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
	private float getEnrollmentFee(long id, List<RegisteredAthleteDao> familyAthltes, boolean isEnrollmentFeePaid,
			FeeDto sportSchoolFee, String membershipType) {

		float fee = 0;

		// If any of the relatives has paid the enrollment fee, the fee for this athlete
		// is 1/2 fee.
		// If not, one of the new athletes pays the whole fee and the rest pays 1/2 fee
		boolean existing = familyAthltes.stream().anyMatch(f -> f.isEnrollmentFeePaid());

		if (membershipType == null || (!membershipType.equals(MembershipTypeEnum.SUBSIDIZED.name())
				&& !membershipType.equals(MembershipTypeEnum.DIRECTOR.name())
				&& !membershipType.equals(MembershipTypeEnum.TRAINER.name()))) {
			if (id == 0 || !isEnrollmentFeePaid) {
				fee = sportSchoolFee.getEnrollmentFee();
				if (sportSchoolFee.getSportSchoolId() != 5 && familyAthltes.size() > 1) {
					if (existing) {
						fee *= 0.5;
					} else {
						if (id != familyAthltes.stream().mapToLong(v -> v.getId()).min().getAsLong()) {
							fee *= 0.5;
						}
					}
				}
			}
		}
		return fee;
	}

	private float getMonthlyFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee)
			throws ParseException {

		float trimestralFee = 0;

		if (!athlete.getPaymentType().equals(PaymentType.Subvencionado.label)) {

			if (athlete.getFeeTypeName() != null && (athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB.label)
					|| athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB_ANUAL.label))) {
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
				}
				if (athlete.getFeeTypeName() != null && athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB_ANUAL.label)) {
					trimestralFee *= (10. / 3);
				} else {

					int diffMonths = getPaymentMonths(receiptDto.getStartDate(), receiptDto.getEndDate(),
							athlete.getLastRegisterDate(), athlete.getLastUnregisterDate());
					trimestralFee *= (float) diffMonths / 3;
				}
			}
		}
		return trimestralFee;

	}

	private int getPaymentMonths(Date startDate, Date endDate, LocalDate registerDate, LocalDate unregisterDate) {

		int numMonths = 0;
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(startDate);

		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(endDate);

		while (startDateCalendar.before(endDateCalendar)) {
			if (registerDate != null) {
				Calendar registerDateCalendar = Calendar.getInstance();
				registerDateCalendar.setTime(Date.from(registerDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

				if (startDateCalendar.get(Calendar.MONTH) == registerDateCalendar.get(Calendar.MONTH)
						&& startDateCalendar.get(Calendar.YEAR) == registerDateCalendar.get(Calendar.YEAR)) {
					if (registerDateCalendar.get(Calendar.DAY_OF_MONTH) < 26) {
						numMonths++;
					}
				} else {
					numMonths++;
				}
			}
			if (unregisterDate != null) {
				Calendar unregisterDateCalendar = Calendar.getInstance();
				unregisterDateCalendar
						.setTime(Date.from(registerDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

				if (startDateCalendar.get(Calendar.MONTH) == unregisterDateCalendar.get(Calendar.MONTH)
						&& startDateCalendar.get(Calendar.YEAR) == unregisterDateCalendar.get(Calendar.YEAR)) {
					if (unregisterDateCalendar.get(Calendar.DAY_OF_MONTH) > 10) {
						numMonths++;
					}
				} else {
					numMonths++;
				}
			}

			startDateCalendar.add(Calendar.MONTH, 1);
		}
		return numMonths;
	}

}
