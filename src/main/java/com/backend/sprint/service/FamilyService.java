package com.backend.sprint.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.FamilyDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.repository.FamilyRepository;

@Service
public class FamilyService {

	@Autowired
	private FamilyRepository repository;

	@Autowired
	private AthleteService athleteService;

	public Page<FamilyDto> findPagintation(Specification<FamilyDao> specification, Pageable pageable) {
		Page<FamilyDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<FamilyDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public FamilyDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public FamilyDto findByCode(long code) {
		return convertToDto(repository.findByCode(code));
	}

	public FamilyDto save(FamilyDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public FamilyDto findBySurnames(String firstSurname, String secondSurname) {
		return new ModelMapper().map(repository.findBySurnames(firstSurname, secondSurname), FamilyDto.class);
	}

	private FamilyDto convertToDto(FamilyDao dao) {
		if (dao == null) {
			return null;
		}
		FamilyDto dto = new ModelMapper().map(dao, FamilyDto.class);
		Set<AthleteDto> athletes = athleteService.findByFamily(dto.getId());
		if (athletes != null) {
			dto.setAthleteIds(athletes.stream().map(AthleteDto::getId).collect(Collectors.toSet()));
		}
		return dto;
	}

	private FamilyDao convertToDao(FamilyDto dao) {
		return new ModelMapper().map(dao, FamilyDao.class);
	}

}
