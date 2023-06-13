package com.backend.sprint.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.HistoricRepository;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.HistoricType;

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

	public List<HistoricDto> findAthleteHistoric(long id) {
		return repository.findAthleteHistoric(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public HistoricDto save(HistoricDto dto) throws DataIntegrityViolationException {

		HistoricDto historic = convertToDto(repository.save(convertToDao(dto)));

		long athleteId = historic.getAthleteId();

		AthleteDto athlete = athleteService.findById(athleteId);
		Set<HistoricDto> historics = athlete.getHistoric();
		if (historics == null) {
			historics = new HashSet<HistoricDto>();
		}
		historics.add(historic);
		athlete.setLastRegisterDate(AthleteUtils.getLastRegisterDate(historics));
		athlete.setLastUnregisterDate(AthleteUtils.getLastUnregisterDate(historics));
		athleteService.save(athlete);

		return historic;
	}

	public HistoricDto register(int id, HistoricDto historic) {
		historic.setAthleteId(id);
		historic.setType(HistoricType.REGISTER.name());

		return this.save(historic);
	}

	public HistoricDto unregister(int id, HistoricDto historic) {

		// Remove athlete from all groups
		AthleteDto athlete = athleteService.findById(id);
		athleteService.save(athlete);

		historic.setAthleteId(id);
		historic.setType(HistoricType.UNREGISTER.name());

		return this.save(historic);
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
