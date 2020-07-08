package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.GroupDao;
import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.GroupRepository;
import com.backend.sprint.repository.ScheduleRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.repository.TrainerRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository repository;

	@Autowired
	private AthleteRepository athleteRepository;

	@Autowired
	private TrainerRepository trainerRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public Page<GroupDto> findPagintation(Specification<GroupDao> specification, Pageable pageable) {
		Page<GroupDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<GroupDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public GroupDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public GroupDto findByName(String name) {
		return convertToDto(repository.findByName(name));
	}

	public GroupDto save(GroupDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public GroupDto convertToDto(GroupDao dao) {
		if (dao == null) {
			return null;
		}
		GroupDto dto = new ModelMapper().map(dao, GroupDto.class);
		if (dao.getTrainer() != null) {
			dto.setTrainerId(dao.getTrainer().getId());
		}
		if (dao.getSportSchool() != null) {
			dto.setSportSchoolId(dao.getSportSchool().getId());
		}
		if (dao.getAthletes() != null) {
			dto.setAthleteIds(dao.getAthletes().stream().map(AthleteDao::getId).collect(Collectors.toSet()));
		}
		if (dao.getSchedules() != null) {
			dto.setScheduleIds(dao.getSchedules().stream().map(ScheduleDao::getId).collect(Collectors.toSet()));
		}
		return dto;
	}

	public GroupDao convertToDao(GroupDto dto) {
		if (dto == null) {
			return null;
		}

		GroupDao dao = new ModelMapper().map(dto, GroupDao.class);
		if (dto.getAthleteIds() != null) {
			dao.setAthletes(dto.getAthleteIds().stream().map(athleteId -> {
				return athleteRepository.findById(athleteId).get();
			}).collect(Collectors.toSet()));
		}
		if (dto.getScheduleIds() != null) {
			dao.setSchedules(dto.getScheduleIds().stream().map(scheduleId -> {
				return scheduleRepository.findById(scheduleId).get();
			}).collect(Collectors.toSet()));
		}
		if (dto.getTrainerId() != 0) {
			dao.setTrainer(trainerRepository.findById(dto.getTrainerId()).get());
		}
		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}
		return dao;
	}

}
