package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.repository.ScheduleRepository;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository repository;

	public List<ScheduleDto> findAll() {
		return repository.findAll().stream().map(dao -> {
			return new ModelMapper().map(dao, ScheduleDto.class);
		}).collect(Collectors.toList());
	}

	public ScheduleDto findById(long id) {
		ScheduleDao dao = repository.findById(id).get();
		return new ModelMapper().map(dao, ScheduleDto.class);
	}

	public ScheduleDto save(ScheduleDto dto) {
		ScheduleDao dao = new ModelMapper().map(dto, ScheduleDao.class);
		return new ModelMapper().map(repository.save(dao), ScheduleDto.class);
	}

}
