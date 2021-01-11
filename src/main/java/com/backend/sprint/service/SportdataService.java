package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.SportdataDao;
import com.backend.sprint.model.dto.SportdataDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.SportdataRepository;

@Service
@Transactional
public class SportdataService {

	@Autowired
	private SportdataRepository repository;

	@Autowired
	private AthleteRepository athleteRepository;

	public Page<SportdataDto> findPagintation(Specification<SportdataDao> specification, Pageable pageable) {
		Page<SportdataDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<SportdataDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public SportdataDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public List<SportdataDto> findByAthleteId(long id) {
		return repository.findByAthleteId(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional
	public SportdataDto save(SportdataDto dto) throws DataIntegrityViolationException {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private SportdataDto convertToDto(SportdataDao dao) {
		if (dao == null) {
			return null;
		}
		SportdataDto dto = new ModelMapper().map(dao, SportdataDto.class);
		dto.setAthleteId(dao.getAthlete().getId());
		dto.setCategory(dao.getAthlete().getCategory());
		return dto;
	}

	private SportdataDao convertToDao(SportdataDto dto) {
		SportdataDao dao = new ModelMapper().map(dto, SportdataDao.class);
		if (dto.getAthleteId() != 0) {
			dao.setAthlete(athleteRepository.findById(dto.getAthleteId()).get());
		}
		return dao;
	}

}
