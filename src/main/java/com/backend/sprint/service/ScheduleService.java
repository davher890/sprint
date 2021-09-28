package com.backend.sprint.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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

	public Page<ScheduleDto> findPagintation(Specification<ScheduleDao> specification, Pageable pageable, String sort) {
		Sort sortOrder = null;
		if (sort != null) {
			sortOrder = Sort.by(sort);
		}
		Page<ScheduleDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ScheduleDto> findAll() {
		return StreamSupport
				.stream(repository.findAll(Sort.by(Order.by("day"), Order.by("startHour"))).spliterator(), false)
				.map(this::convertToDto).collect(Collectors.toList());
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

	public ScheduleDto convertToDto(ScheduleDao dao) {
		if (dao == null) {
			return null;
		}
		ScheduleDto map = new ModelMapper().map(dao, ScheduleDto.class);
		map.setDayTranslate(dayTranslate.get(map.getDay()));
		return map;
	}

	public Set<ScheduleDto> convertToDto(Set<ScheduleDao> dao) {
		return dao.parallelStream().map(this::convertToDto).collect(Collectors.toSet());
	}

	private ScheduleDao convertToDao(ScheduleDto dto) {
		if (dto == null) {
			return null;
		}
		return new ModelMapper().map(dto, ScheduleDao.class);
	}

}
