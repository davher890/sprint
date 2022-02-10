package com.backend.sprint.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.repository.RegisteredAthleteRepository;

@Service
@Transactional
public class RegisteredAthleteService {

	@Autowired
	private RegisteredAthleteRepository repository;

	public Page<AthleteDto> findPagintation(Specification<RegisteredAthleteDao> specification, Pageable pageable) {
		Page<RegisteredAthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<RegisteredAthleteDao> findDaoPagintation(Specification<RegisteredAthleteDao> specification,
			Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	private AthleteDto convertToDto(RegisteredAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		return new ModelMapper().map(dao, AthleteDto.class);
	}

}
