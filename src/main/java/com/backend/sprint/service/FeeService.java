package com.backend.sprint.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.repository.FeeRepository;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
@Transactional
public class FeeService {

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
}
