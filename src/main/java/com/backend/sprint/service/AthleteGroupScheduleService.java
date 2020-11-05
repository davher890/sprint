package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteGroupScheduleDao;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.repository.AthleteGroupScheduleRepository;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.GroupRepository;
import com.backend.sprint.repository.ScheduleRepository;

@Service
public class AthleteGroupScheduleService {

	@Autowired
	private AthleteGroupScheduleRepository repository;

	@Autowired
	private AthleteRepository athleteRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	public List<AthleteGroupScheduleDto> findByAthlete(long athleteId) {
		return repository.findByAthlete(athleteId).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteGroupScheduleDto> findByGroup(long groupId) {
		return repository.findByGroup(groupId).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteGroupScheduleDto> findBySchedule(long scheduleId) {
		return repository.findBySchedule(scheduleId).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AthleteGroupScheduleDto> findByGroupAndSchedule(long groupId, long scheduleId) {
		return repository.findByGroupAndSchedule(groupId, scheduleId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public AthleteGroupScheduleDto save(AthleteGroupScheduleDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public AthleteGroupScheduleDto convertToDto(AthleteGroupScheduleDao dao) {
		if (dao == null) {
			return null;
		}
		AthleteGroupScheduleDto dto = new ModelMapper().map(dao, AthleteGroupScheduleDto.class);
		//
		// if (dao.getId().getAthlete() != null) {
		// dto.setAthleteId(dao.getId().getAthlete().getId());
		// //
		// dto.setAthlete(athleteService.findById(dao.getId().getAthlete().getId()));
		// }
		// if (dao.getId().getGroup() != null) {
		// dto.setGroupId(dao.getId().getGroup().getId());
		// //
		// dto.setGroup(groupService.findById(dao.getId().getGroup().getId()));
		// }
		// if (dao.getId().getSchedule() != null) {
		// dto.setScheduleId(dao.getId().getSchedule().getId());
		// //
		// dto.setSchedule(scheduleService.findById(dao.getId().getSchedule().getId()));
		// }
		return dto;
	}

	public AthleteGroupScheduleDao convertToDao(AthleteGroupScheduleDto dto) {
		if (dto == null) {
			return null;
		}

		AthleteGroupScheduleDao dao = new ModelMapper().map(dto, AthleteGroupScheduleDao.class);
		//
		// AthleteGroupScheduleId id = new AthleteGroupScheduleId();
		// id.setAthlete(athleteRepository.findById(dto.getAthleteId()).get());
		// id.setGroup(groupRepository.findById(dto.getGroupId()).get());
		// id.setSchedule(scheduleRepository.findById(dto.getScheduleId()).get());

		// dao.setId(id);
		// dao.setAthleteId(dto.getAthleteId());
		// dao.setGroupId(dto.getGroupId());
		// dao.setScheduleId(dto.getScheduleId());
		return dao;
	}

	@Transactional
	public void deleteByAthlete(long athleteId) {
		repository.deleteByAthlete(athleteId);
	}
}
