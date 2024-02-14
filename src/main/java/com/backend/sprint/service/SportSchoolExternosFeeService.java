package com.backend.sprint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.ReceiptDto;
import com.backend.sprint.utils.FeeTypeEnum;
import com.backend.sprint.utils.MembershipTypeEnum;
import com.backend.sprint.utils.PaymentType;

@Service
@Transactional
public class SportSchoolExternosFeeService extends SportSchoolFeeService {

	@Override
	public float getLicenseFee(long sportSchoolId, long feeTypeId, String membershipType, String receiptType,
			float territorialLicenseFee) {
		float fee = 0;

		if (feeTypeId == FeeTypeEnum.FED.id || (feeTypeId == FeeTypeEnum.SOCIO.id && membershipType != null
				&& membershipType.equals(MembershipTypeEnum.ATHLETE.name()))) {
			fee = territorialLicenseFee;
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
							athlete.getHistoric());
					trimestralFee *= (float) diffMonths / 3;
				}
			}
		}
		return trimestralFee;

	}

}
