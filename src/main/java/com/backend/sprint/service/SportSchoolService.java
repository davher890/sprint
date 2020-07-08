package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
public class SportSchoolService {

	@Autowired
	private SportSchoolRepository repository;

	public List<SportSchoolDto> findAll() {
		return repository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public SportSchoolDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public SportSchoolDto findByName(String name) {
		return convertToDto(repository.findByName(name));
	}

	public SportSchoolDto save(SportSchoolDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private SportSchoolDto convertToDto(SportSchoolDao dao) {
		if (dao == null) {
			return null;
		}
		return new ModelMapper().map(dao, SportSchoolDto.class);
	}

	private SportSchoolDao convertToDao(SportSchoolDto dto) {
		if (dto == null) {
			return null;
		}
		return new ModelMapper().map(dto, SportSchoolDao.class);
	}

}
