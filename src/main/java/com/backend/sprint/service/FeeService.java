package com.backend.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;

@Service
public class FeeService {

	@Autowired
	private FamilyService familyService;

	public FeeDto getAthleteFee(AthleteDto dto) {

		FeeDto fee = new FeeDto();
		fee.setMonthlyFee(this.getMonthlyFee(dto));

		fee.setEnrollmentFee(this.getEnrollmentFee(dto));

		fee.setMembershipFee(this.getMembershipFee());

		return fee;

	}

	// Annually
	public int getMembershipFee() {
		return 20;
	}

	// Once
	public int getEnrollmentFee(AthleteDto dto) {

		int fee = 0;

		if (dto.getSportSchoolId() == 3) {
			fee = 10;
		} else {
			if (dto.getFamilyCode() > 0) {
				FamilyDto family = familyService.findByCode(dto.getFamilyCode());
				// if (dto.getLicenseType().equals("PISTAS") ||
				// dto.getLicenseType().equals("LICENCIA")) {
				if (family.getCount() > 1) {
					fee = 20;
				} else {
					fee = 40;
				}
				// A partir del tercer miembro solo se paga una cuota de socio por
				// familia
			}

		}
		return fee;
	}

	public int getMonthlyFee(AthleteDto dto) {

		int fee = 0;

		if (dto.isSpecialization()) {
			fee = 100;
		} else {
			if (dto.getScheduleIds() != null) {
				switch (dto.getScheduleIds().size()) {
				case 1:
					if (dto.getSportSchoolId() == 1) {
						fee = 55;
					} else if (dto.getSportSchoolId() == 2) {
						fee = 50;
					}
					break;
				case 2:
					if (dto.getSportSchoolId() == 1) {
						fee = 95;
					} else if (dto.getSportSchoolId() == 2) {
						fee = 85;
					}
					break;
				case 3:
				default:
					if (dto.getSportSchoolId() == 1) {
						fee = 115;
					} else if (dto.getSportSchoolId() == 2) {
						fee = 100;
					}
					break;
				}
			}
		}

		return fee;
	}
}
