package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.repository.AthleteRepository;

@Service
public class AthleteService {

	@Autowired
	private AthleteRepository repository;

	public List<AthleteDto> findAll() {
		return repository.findAll().stream().map(dao -> {
			return new ModelMapper().map(dao, AthleteDto.class);
		}).collect(Collectors.toList());
	}

	public AthleteDto findById(long id) {
		AthleteDao dao = repository.findById(id).get();
		return new ModelMapper().map(dao, AthleteDto.class);
	}

	public AthleteDto save(AthleteDto dto) {
		AthleteDao dao = new ModelMapper().map(dto, AthleteDao.class);
		return new ModelMapper().map(repository.save(dao), AthleteDto.class);
	}

}
