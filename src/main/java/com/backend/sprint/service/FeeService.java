package com.backend.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.LicenseTypeCategoryFeeDao;
import com.backend.sprint.model.dto.AthleteDto;
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

	public FeeDto getAthleteFee(AthleteDto dto) {

		FeeDto fee = new FeeDto();
		fee.setMonthlyFee(this.getMonthlyFee(dto));

		fee.setEnrollmentFee(this.getEnrollmentFee(dto));

		fee.setMembershipFee(this.getMembershipFee(dto));

		return fee;

	}

	// Annually
	public float getMembershipFee(AthleteDto dto) {

		float fee = 0;

		if (!dto.getPaymentType().equals(PaymentType.Subvencionado.label)) {
			fee = 20;
			FamilyDto family = familyService.findByCode(dto.getFamilyCode());
			if (family != null) {
				int familyCount = family.getCount();
				if (familyCount > 0) {

					// Already registered athlete
					if (dto.getId() > 0) {
						if (familyCount > 3) {
							fee /= familyCount;
						}
					}
					// New athlete
					if (dto.getId() == 0) {
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
	public int getEnrollmentFee(AthleteDto dto) {

		int fee = 0;
		if (dto.getId() == 0) {
			fee = 40;

			if (dto.getSportSchoolId() == 3) {
				fee = 10;
			} else {
				if (dto.getFamilyCode() > 0) {
					FamilyDto family = familyService.findByCode(dto.getFamilyCode());
					// if (dto.getLicenseType().equals("PISTAS") ||
					// dto.getLicenseType().equals("LICENCIA")) {
					if (dto.getId() == 0) {
						if (family.getCount() > 0) {
							fee = 20;
						}
					}
					if (dto.getId() > 0) {
						if (family.getCount() > 1) {
							fee = 20;
						}
					}

					// A partir del tercer miembro solo se paga una cuota de socio por
					// familia
				}

			}
		}
		return fee;
	}

	public int getMonthlyFee(AthleteDto dto) {

		int fee = 0;

		LicenseTypeCategoryFeeDao licenseTypeCategoryFee = repository.findByLicenseTypeAndCategory(dto.getLicenseType(),
				dto.getCategory());

		if (!dto.getPaymentType().equals(PaymentType.Subvencionado.label)) {
			if (dto.getSportSchoolId() == 1) {
				if (dto.isSpecialization()) {
					fee = 115;
				} else {
					if (dto.getScheduleIds() != null) {
						switch (dto.getScheduleIds().size()) {
						case 0:
							fee = 0;
							break;
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
					}
				}
			}
			if (dto.getSportSchoolId() == 2) {
				if (dto.isSpecialization()) {
					fee = 100;
				} else {
					if (dto.getScheduleIds() != null) {
						switch (dto.getScheduleIds().size()) {
						case 0:
							fee = 0;
							break;
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
					}
				}

			}

//			if (dto.getSportSchoolId() == 3) {
//				Category catEnum = Category.valueOf(dto.getCategory());
//				switch (catEnum) {
//				case Category.SUB8:
//					
//				
//				}
//				if (dto.getLicenseType().equals(LicenseType.National.name())) {
//					 
//
//				}
//				if (dto.getCategory().equals("Territorial")) {
//
//				}
//			}
		}

		return fee;
	}
}
