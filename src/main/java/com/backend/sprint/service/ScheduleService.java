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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.ScheduleRepository;
import com.backend.sprint.repository.UserRepository;

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

	@Autowired
	private UserRepository userRepository;

	public Page<ScheduleDto> findPagintation(Specification<ScheduleDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<ScheduleDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<ScheduleDto> findAll() {
		return StreamSupport.stream(repository
				.findAll(Sort.by(Order.by("day"), Order.by("startHour"), Order.by("startMinute"))).spliterator(), false)
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public ScheduleDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public ScheduleDto findByTime(String day, int startHour, int startMinute, int endHour, int endMinute) {
		ScheduleDao dao = repository.findByTime(day, startHour, startMinute, endHour, endMinute);
		return convertToDto(dao);
	}

	public List<ScheduleDto> findByGroupId(long groupId) {
		return repository.findByGroupId(groupId).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public ScheduleDto save(ScheduleDto dto) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setUpdatedById(user.getId());

		return convertToDto(repository.save(convertToDao(dto)));
	}

	public ScheduleDto convertToDto(ScheduleDao dao) {
		if (dao == null) {
			return null;
		}
		ScheduleDto dto = new ModelMapper().map(dao, ScheduleDto.class);
		dto.setDayTranslate(dayTranslate.get(dto.getDay()));

		UserDao updatedBy = dao.getUpdatedBy();
		if (updatedBy != null) {
			dto.setUpdatedById(updatedBy.getId());
		}

		return dto;
	}

	public String translateDay(String day) {
		return dayTranslate.get(day);
	}

	public Set<ScheduleDto> convertToDto(Set<ScheduleDao> dao) {
		return dao.parallelStream().map(this::convertToDto).collect(Collectors.toSet());
	}

	private ScheduleDao convertToDao(ScheduleDto dto) {
		if (dto == null) {
			return null;
		}
		ScheduleDao dao = new ModelMapper().map(dto, ScheduleDao.class);

		long updatedById = dto.getUpdatedById();
		if (updatedById != 0) {
			dao.setUpdatedBy(userRepository.findById(updatedById).get());
		}

		return dao;
	}

}
