package com.backend.sprint.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.repository.ScheduleRepository;

@Service
@Transactional
public class ScheduleService {

	private final Map<String, String> dayTranslate = new HashMap<String, String>() {
		{
			put("MONDAY", "Lunes");
			put("TUESDAY", "Martes");
			put("WEDNESDAY", "Miercoles");
			put("THURSDAY", "Jueves");
			put("FRIDAY", "Viernes");
			put("SATURDAY", "Sabado");
			put("SUNDAY", "Domingo");
		}
	};

	@Autowired
	private ScheduleRepository repository;

	public Page<ScheduleDto> findPagintation(Specification<ScheduleDao> specification, Pageable pageable) {
		Page<ScheduleDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ScheduleDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public ScheduleDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public ScheduleDto findByTime(String day, int startHour, int startMinute, int endHour, int endMinute) {
		ScheduleDao dao = repository.findByTime(day, startHour, startMinute, endHour, endMinute);
		return convertToDto(dao);
	}

	public ScheduleDto save(ScheduleDto dto) {
		ScheduleDao dao = convertToDao(dto);
		return convertToDto(repository.save(dao));
	}

	private ScheduleDto convertToDto(ScheduleDao dao) {
		if (dao == null) {
			return null;
		}
		ScheduleDto map = new ModelMapper().map(dao, ScheduleDto.class);
		map.setDayTranslate(dayTranslate.get(map.getDay()));
		return map;
	}

	private ScheduleDao convertToDao(ScheduleDto dto) {
		if (dto == null) {
			return null;
		}
		return new ModelMapper().map(dto, ScheduleDao.class);
	}

}
