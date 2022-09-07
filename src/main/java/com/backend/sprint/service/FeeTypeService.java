package com.backend.sprint.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeTypeDao;
import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.FeeTypeDto;
import com.backend.sprint.repository.FeeTypeRepository;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
@Transactional
public class FeeTypeService {

	@Autowired
	private FeeTypeRepository repository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public FeeTypeDto save(FeeTypeDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public FeeTypeDto convertToDto(FeeTypeDao dao) {
		if (dao == null) {
			return null;
		}
		FeeTypeDto dto = new ModelMapper().map(dao, FeeTypeDto.class);

		SportSchoolDao sportSchool = dao.getSportSchool();
		if (sportSchool != null) {
			dto.setSportSchoolId(sportSchool.getId());
		}
		return dto;
	}

	public FeeTypeDao convertToDao(FeeTypeDto dto) {
		if (dto == null) {
			return null;
		}

		FeeTypeDao dao = new ModelMapper().map(dto, FeeTypeDao.class);
		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}
		return dao;
	}

}
