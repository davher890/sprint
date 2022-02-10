package com.backend.sprint.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.UnregisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.repository.UnregisteredAthleteRepository;

@Service
@Transactional
public class UnregisteredAthleteService {

	@Autowired
	private UnregisteredAthleteRepository repository;

	public Page<AthleteDto> findPagintation(Specification<UnregisteredAthleteDao> specification, Pageable pageable) {
		Page<UnregisteredAthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<UnregisteredAthleteDao> findDaoPagintation(Specification<UnregisteredAthleteDao> specification,
			Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	private AthleteDto convertToDto(UnregisteredAthleteDao dao) {
		if (dao == null) {
			return null;
		}
		return new ModelMapper().map(dao, AthleteDto.class);
	}

}
