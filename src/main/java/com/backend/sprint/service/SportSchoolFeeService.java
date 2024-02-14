package com.backend.sprint.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.RegisteredAthleteRepository;
import com.backend.sprint.utils.HistoricType;

@Service
@Transactional
public abstract class SportSchoolFeeService {

	@Autowired
	private AthleteRepository athleteRepository;

	@Autowired
	private RegisteredAthleteRepository registeredAthleteRepository;

	public FeeDto getAthleteFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee)
			throws ParseException {

		List<AthleteDao> athletes = athleteRepository.findByFamilyId(athlete.getFamilyId());
		List<RegisteredAthleteDao> familyAthltes = null;
		if (athletes != null) {
			Set<Long> familyAthleteIds = athletes.stream().map(AthleteDao::getId).collect(Collectors.toSet());
			familyAthltes = registeredAthleteRepository.findByAthleteIds(new ArrayList<Long>(familyAthleteIds));
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

	public abstract float getLicenseFee(long sportSchoolId, long feeTypeId, String membershipType, String receiptType,
			float territorialLicenseFee);

	// Annually
	public abstract float getMembershipFee(long id, String paymentType, String feeType,
			List<RegisteredAthleteDao> familyAthltes, boolean isMembershipFeePaid, FeeDto sportSchoolFee);

	// Once
	public abstract float getEnrollmentFee(long id, List<RegisteredAthleteDao> familyAthltes,
			boolean isEnrollmentFeePaid, FeeDto sportSchoolFee, String membershipType);

	public abstract float getMonthlyFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee);

	protected int getPaymentMonths(Date startDate, Date endDate, List<HistoricDto> historic) {

		int numMonths = 0;

		List<HistoricDto> sortedHistoric = historic.stream()
				.filter(d -> d.getDate().isBefore(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
				.sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList());

		LocalDate registerLocalDate = null;
		LocalDate unregisterLocalDate = null;
		Date registerDate = null;
		Date unregisterDate = null;
		if (!sortedHistoric.isEmpty()) {
			if (sortedHistoric.get(0).getType().equals(HistoricType.REGISTER.name())) {
				registerLocalDate = sortedHistoric.get(0).getDate();
				registerDate = Date.from(registerLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
			if (sortedHistoric.get(0).getType().equals(HistoricType.UNREGISTER.name())) {
				unregisterLocalDate = sortedHistoric.get(0).getDate();
				unregisterDate = Date.from(unregisterLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
		}

		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(startDate);

		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(endDate);

		while (!(startDateCalendar.get(Calendar.YEAR) == endDateCalendar.get(Calendar.YEAR)
				&& startDateCalendar.get(Calendar.MONTH) == endDateCalendar.get(Calendar.MONTH))) {
			startDateCalendar.set(Calendar.DAY_OF_MONTH, 25);
			if (registerDate != null && registerDate.before(startDateCalendar.getTime())) {

				Calendar registerDateCalendar = Calendar.getInstance();
				registerDateCalendar.setTime((registerDate.before(startDate) ? startDate : registerDate));

				if (startDateCalendar.get(Calendar.MONTH) == registerDateCalendar.get(Calendar.MONTH)
						&& startDateCalendar.get(Calendar.YEAR) == registerDateCalendar.get(Calendar.YEAR)) {
					if (registerDateCalendar.get(Calendar.DAY_OF_MONTH) < 26) {
						numMonths++;
					}
				} else {
					numMonths++;
				}
			}
			startDateCalendar.set(Calendar.DAY_OF_MONTH, 10);
			if (unregisterDate != null && unregisterDate.after(startDateCalendar.getTime())) {
				Calendar unregisterDateCalendar = Calendar.getInstance();
				unregisterDateCalendar.setTime((unregisterDate.after(endDate) ? endDate : unregisterDate));

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
