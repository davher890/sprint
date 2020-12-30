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

import com.backend.sprint.model.dao.HistoricDao;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.HistoricRepository;

@Service
@Transactional
public class HistoricService {

	@Autowired
	private HistoricRepository repository;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private AthleteRepository athleteRepository;

	public Page<HistoricDto> findPagintation(Specification<HistoricDao> specification, Pageable pageable) {
		Page<HistoricDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<HistoricDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public HistoricDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public List<HistoricDto> findAthleteRegistration(long id) {
		return repository.findAthleteRegistration(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional
	public HistoricDto save(HistoricDto dto) throws DataIntegrityViolationException {
		dto = convertToDto(repository.save(convertToDao(dto)));
		athleteService.updateAthleteRegistrationDate(athleteService.findById(dto.getAthleteId()));
		return dto;
	}

	private HistoricDto convertToDto(HistoricDao dao) {
		if (dao == null) {
			return null;
		}
		HistoricDto dto = new ModelMapper().map(dao, HistoricDto.class);
		dto.setAthleteId(dao.getAthlete().getId());
		return dto;
	}

	private HistoricDao convertToDao(HistoricDto dto) {
		HistoricDao dao = new ModelMapper().map(dto, HistoricDao.class);
		if (dto.getAthleteId() != 0) {
			dao.setAthlete(athleteRepository.findById(dto.getAthleteId()).get());
		}
		return dao;
	}

}
