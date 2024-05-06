package com.backend.sprint.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.ReceiptAthleteDao;
import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.ReceiptAthleteRepository;
import com.backend.sprint.repository.RegisteredAthleteRepository;
import com.backend.sprint.service.SportSchoolFeeService;
import com.backend.sprint.utils.FeeTypeEnum;
import com.backend.sprint.utils.MembershipTypeEnum;
import com.backend.sprint.utils.PaymentType;
import com.backend.sprint.utils.ReceiptStatus;

@Service
@Transactional
public class SportSchoolParacuellosFeeService extends SportSchoolFeeService {

	@Autowired
	private ReceiptAthleteRepository receiptAthleteRepository;

	@Autowired
	private AthleteRepository athleteRepository;

	@Autowired
	private RegisteredAthleteRepository registeredAthleteRepository;

	@Override
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

		// Externos
		float nationalLicenseFee = this.getLicenseFee(sportSchoolFee.getSportSchoolId(), athlete.getFeeTypeId(),
				athlete.getMembershipType(), receiptDto.getReceiptType(), athlete.getLicenseNationalFee());
		fee.setLicenseNationalFee(nationalLicenseFee);

		float territorialLicenseFee = this.getLicenseFee(sportSchoolFee.getSportSchoolId(), athlete.getFeeTypeId(),
				athlete.getMembershipType(), receiptDto.getReceiptType(), athlete.getLicenseTerritorialFee());
		fee.setLicenseTerritorialFee(territorialLicenseFee);

		float licenseOtherFee = this.getLicenseFee(sportSchoolFee.getSportSchoolId(), athlete.getFeeTypeId(),
				athlete.getMembershipType(), receiptDto.getReceiptType(), athlete.getLicenseOtherFee());
		fee.setLicenseOtherFee(licenseOtherFee);

		// Look for season receipts.
		List<ReceiptAthleteDao> receipts = receiptAthleteRepository.findByAthleteIdAndReceiptStatusAndSeason(
				athlete.getId(), ReceiptStatus.VALIDATED.name(), receiptDto.getSeason());

		if (receipts != null && !receipts.isEmpty()) {
			// There are more previous receipts.
			if (athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB.label)) {
				// Season Trimestral receipt already paid.
				boolean paidReceipt = receipts.stream().anyMatch(
						(ReceiptAthleteDao d) -> d.getReceipt().getStartDate().compareTo(receiptDto.getStartDate()) == 0
								&& d.getReceipt().getEndDate().compareTo(receiptDto.getEndDate()) == 0
								&& d.getStatus().equals(ReceiptStatus.PAID.name()));
				if (paidReceipt) {
					fee.setMonthlyFee(0);
				} else {
					// Pending receipts from the same season.
					boolean pendingReceipts = receipts.stream()
							.anyMatch((ReceiptAthleteDao d) -> d.getStatus().equals(ReceiptStatus.RETURN.name()));
					// Remove athlete
					if (pendingReceipts) {
						fee.setMonthlyFee(0);
						fee.setEnrollmentFee(0);
						fee.setMembershipFee(0);
						fee.setLicenseNationalFee(0);
						fee.setLicenseTerritorialFee(0);
						fee.setLicenseOtherFee(0);
					} else {
						boolean errorReceipts = receipts.stream().anyMatch(
								(ReceiptAthleteDao d) -> d.getStatus().equals(ReceiptStatus.SENT_BY_ERROR.name()));
						if (errorReceipts && !athlete.isRegistered()) {
							fee.setMonthlyFee(0);
							fee.setEnrollmentFee(0);
							fee.setMembershipFee(0);
							fee.setLicenseNationalFee(0);
							fee.setLicenseTerritorialFee(0);
							fee.setLicenseOtherFee(0);
						}
					}
				}
			} else if (athlete.getFeeTypeName().equals(FeeTypeEnum.CLUB_ANUAL.label)) {
				// Annual fee already paid
				if (receipts.stream()
						.anyMatch((ReceiptAthleteDao d) -> d.getStatus().equals(ReceiptStatus.PAID.name()))) {
					fee.setMonthlyFee(0);
					fee.setEnrollmentFee(0);
					fee.setMembershipFee(0);
					fee.setLicenseNationalFee(0);
					fee.setLicenseTerritorialFee(0);
				}
			}
		}

		return fee;
	}

	@Override
	public float getLicenseFee(long sportSchoolId, long feeTypeId, String membershipType, String receiptType,
			float licenseFee) {
		float fee = 0;

		if (receiptType != null && receiptType.equals("LICENSE")) {
			fee = licenseFee;
		}
		return fee;

	}

	@Override
	public float getMembershipFee(long id, String paymentType, String feeType, List<RegisteredAthleteDao> familyAthltes,
			boolean isMembershipFeePaid, FeeDto sportSchoolFee) {

		if (paymentType.equals(PaymentType.Subvencionado.label)
				|| (feeType != null && feeType.equals(FeeTypeEnum.CLUB_ANUAL.label)) || isMembershipFeePaid) {
			return 0;
		} else {

			float fee = sportSchoolFee.getMembershipFee();
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
			return fee;
		}
	}

	@Override
	public float getEnrollmentFee(long id, List<RegisteredAthleteDao> familyAthltes, boolean isEnrollmentFeePaid,
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

	@Override
	public float getMonthlyFee(AthleteDto athlete, ReceiptDto receiptDto, FeeDto sportSchoolFee) {

		if (athlete.getPaymentType().equals(PaymentType.Subvencionado.label)) {
			return 0;
		} else {

			float trimestralFee = 0;

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
							athlete.getHistoric());
					trimestralFee *= (float) diffMonths / 3;
				}
			}
			return trimestralFee;
		}
	}

}
