package com.backend.sprint.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.LicenseTypeCategoryFeeDao;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.repository.LicenseTypeCategoryFeeRepository;
import com.backend.sprint.utils.PaymentType;

@Service
public class FeeService {

	@Autowired
	private LicenseTypeCategoryFeeRepository repository;

	@Autowired
	private FamilyService familyService;

	public FeeDto getAthleteFee(long id, long familyCode, String paymentType, long sportSchoolId,
			boolean specialization, Set<Long> scheduleIds, String category, String licenseType,
			boolean isEnrollmentFeePaid, String collection) {

		FeeDto fee = new FeeDto();
		float monthlyFee = this.getMonthlyFee(paymentType, sportSchoolId, specialization, scheduleIds, category,
				licenseType);
		fee.setMonthlyFee(
				collection != null && (collection.equals("september") || collection.equals("july")) ? monthlyFee / 3
						: monthlyFee);

		fee.setEnrollmentFee(this.getEnrollmentFee(id, sportSchoolId, familyCode, isEnrollmentFeePaid));

		fee.setMembershipFee(this.getMembershipFee(id, paymentType, familyCode));

		return fee;

	}

	// Annually
	public float getMembershipFee(long id, String paymentType, long familyCode) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label)) {
			fee = 20;
			FamilyDto family = familyService.findByCode(familyCode);
			if (family != null) {
				int familyCount = family.getCount();
				if (familyCount > 0) {

					// Already registered athlete
					if (id > 0) {
						if (familyCount > 3) {
							fee /= familyCount;
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
	public int getEnrollmentFee(long id, long sportSchoolId, long familyCode, boolean isEnrollmentFeePaid) {

		int fee = 0;
		if (id == 0 || !isEnrollmentFeePaid) {
			fee = 40;
			if (sportSchoolId == 3) {
				fee = 10;
			} else {
				if (familyCode > 0) {
					FamilyDto family = familyService.findByCode(familyCode);
					// if (dto.getLicenseType().equals("PISTAS") ||
					// dto.getLicenseType().equals("LICENCIA")) {
					if (family.getCount() > 0) {
						fee = 20;
					}
					if (family.getCount() > 1) {
						fee = 20;
					}
				}

				// A partir del tercer miembro solo se paga una cuota de socio por
				// familia
			}

		}
		return fee;

	}

	public float getMonthlyFee(String paymentType, long sportSchoolId, boolean specialization, Set<Long> scheduleIds,
			String category, String licenseType) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label)) {

			if (sportSchoolId == 1) {
				if (specialization) {
					fee = 115;
				} else {
					if (scheduleIds != null && !scheduleIds.isEmpty()) {
						switch (scheduleIds.size()) {
						case 1:
							fee = 55;
							break;
						case 2:
							fee = 95;
							break;
						default:
							fee = 115;
							break;
						}
					} else {
						if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
							LicenseTypeCategoryFeeDao licenseTypeCategoryFee = repository
									.findByLicenseTypeAndCategory(licenseType, category);
							fee = licenseTypeCategoryFee.getFee();
						}
					}
				}
			}
			if (sportSchoolId == 2) {
				if (specialization) {
					fee = 100;
				} else {
					if (scheduleIds != null && !scheduleIds.isEmpty()) {
						switch (scheduleIds.size()) {
						case 1:
							fee = 50;
							break;
						case 2:
							fee = 85;
							break;
						default:
							fee = 100;
							break;
						}
					} else {
						if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
							LicenseTypeCategoryFeeDao licenseTypeCategoryFee = repository
									.findByLicenseTypeAndCategory(licenseType, category);
							fee = licenseTypeCategoryFee.getFee();
						}
					}
				}

			}

			if (sportSchoolId == 3 || sportSchoolId == 5) {
				if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
					LicenseTypeCategoryFeeDao licenseTypeCategoryFee = repository
							.findByLicenseTypeAndCategory(licenseType, category);
					fee = licenseTypeCategoryFee.getFee();
				}
			}
		}

		return fee;
	}
}
