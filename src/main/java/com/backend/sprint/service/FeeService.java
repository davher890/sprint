package com.backend.sprint.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeDao;
import com.backend.sprint.model.dao.LicenseTypeCategoryFeeDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.repository.FeeRepository;
import com.backend.sprint.repository.LicenseTypeCategoryFeeRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.utils.PaymentType;

@Service
@Transactional
public class FeeService {

	@Autowired
	private LicenseTypeCategoryFeeRepository licenseTypeCategoryFeeRepository;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private FeeRepository repository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public FeeDto save(FeeDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public FeeDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public FeeDto findBySportSchoolId(long sportSchoolId) {
		return convertToDto(repository.findBySportSchoolId(sportSchoolId));
	}

	public FeeDto getAthleteFee(long id, long familyCode, String paymentType, boolean specialization, int numDays,
			String category, String licenseType, boolean isEnrollmentFeePaid, boolean isMembershipFeePaid,
			String collection, FeeDto sportSchoolFee) {

		FeeDto fee = new FeeDto();
		float monthlyFee = this.getMonthlyFee(paymentType, specialization, numDays, category, licenseType,
				sportSchoolFee);
		fee.setMonthlyFee(collection != null && (collection.equals("initial_fee")) ? monthlyFee / 3 : monthlyFee);

		float enrollmentFee = this.getEnrollmentFee(id, familyCode, isEnrollmentFeePaid, sportSchoolFee);
		fee.setEnrollmentFee(enrollmentFee);

		float membershipFee = this.getMembershipFee(id, paymentType, familyCode, isMembershipFeePaid, sportSchoolFee);
		fee.setMembershipFee(membershipFee);

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
		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}
		return dao;
	}

	// Annually
	public float getMembershipFee(long id, String paymentType, long familyCode, boolean isMembershipFeePaid,
			FeeDto sportSchoolFee) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label) && !isMembershipFeePaid) {

			fee = sportSchoolFee.getMembershipFee();
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
	public float getEnrollmentFee(long id, long familyCode, boolean isEnrollmentFeePaid, FeeDto sportSchoolFee) {

		float fee = 0;
		if (id == 0 || !isEnrollmentFeePaid) {
			fee = sportSchoolFee.getEnrollmentFee();
			if (sportSchoolFee.getSportSchoolId() == 3) {
				fee = 10;
			} else {
				if (familyCode > 0) {
					FamilyDto family = familyService.findByCode(familyCode);
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

	public float getMonthlyFee(String paymentType, boolean specialization, int numDays, String category,
			String licenseType, FeeDto sportSchoolFee) {

		float fee = 0;

		if (!paymentType.equals(PaymentType.Subvencionado.label)) {

			if (sportSchoolFee.getSportSchoolId() == 1 || sportSchoolFee.getSportSchoolId() == 2) {
				if (specialization) {
					fee = sportSchoolFee.getQuarterSpecialization();
				} else {
					if (numDays > 0) {
						switch (numDays) {
						case 1:
							fee = sportSchoolFee.getQuarterOneDay();
							break;
						case 2:
							fee = sportSchoolFee.getQuarterTwoDays();
							break;
						default:
							fee = sportSchoolFee.getQuarterThreeDays();
							break;
						}
					} else {
						if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
							LicenseTypeCategoryFeeDao licenseTypeCategoryFee = licenseTypeCategoryFeeRepository
									.findByLicenseTypeAndCategory(licenseType, category);
							fee = licenseTypeCategoryFee.getFee();
						}
					}
				}
			}
			if (sportSchoolFee.getSportSchoolId() == 3 || sportSchoolFee.getSportSchoolId() == 5) {
				if (licenseType != null && !licenseType.isEmpty() && category != null && !category.isEmpty()) {
					LicenseTypeCategoryFeeDao licenseTypeCategoryFee = licenseTypeCategoryFeeRepository
							.findByLicenseTypeAndCategory(licenseType, category);
					fee = licenseTypeCategoryFee.getFee();
				}
			}
		}

		return fee;
	}
}
