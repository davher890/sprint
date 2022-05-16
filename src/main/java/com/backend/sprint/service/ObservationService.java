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

import com.backend.sprint.model.dao.ObservationDao;
import com.backend.sprint.model.dto.ObservationDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.ObservationRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class ObservationService {

	@Autowired
	private ObservationRepository repository;

	@Autowired
	private AthleteRepository athleteRepository;

	@Autowired
	private UserRepository userRepository;

	public Page<ObservationDto> findPagintation(Specification<ObservationDao> specification, Pageable pageable) {
		Page<ObservationDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ObservationDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public ObservationDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public List<ObservationDto> findAthleteObservations(long id) {
		return repository.findAthleteObservations(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional
	public ObservationDto save(ObservationDto dto) throws DataIntegrityViolationException {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private ObservationDto convertToDto(ObservationDao dao) {
		if (dao == null) {
			return null;
		}
		ObservationDto dto = new ModelMapper().map(dao, ObservationDto.class);
		dto.setAthleteId(dao.getAthlete().getId());

		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		return dto;
	}

	private ObservationDao convertToDao(ObservationDto dto) {
		ObservationDao dao = new ModelMapper().map(dto, ObservationDao.class);
		if (dto.getAthleteId() != 0) {
			dao.setAthlete(athleteRepository.findById(dto.getAthleteId()).get());
		}

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}

		return dao;
	}

}
