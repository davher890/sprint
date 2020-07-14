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

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.FamilyRepository;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
public class AthleteService {

	@Autowired
	private AthleteRepository repository;

	@Autowired
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	@Autowired
	private FamilyRepository familyRepository;

	public Page<AthleteDto> findPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<AthleteDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public AthleteDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public AthleteDto save(AthleteDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public Set<AthleteDto> findByFamily(long familyId) {
		return repository.findByFamily(familyId).stream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public AthleteDto findByCode(long code) {
		return convertToDto(repository.findByCode(code));
	}

	private AthleteDto convertToDto(AthleteDao dao) {
		if (dao == null) {
			return null;
		}

		AthleteDto dto = new ModelMapper().map(dao, AthleteDto.class);
		if (dao.getSportSchool() != null) {
			dto.setSportSchoolId(dao.getSportSchool().getId());
		}
		if (dao.getFamily() != null) {
			dto.setFamilyId(dao.getFamily().getId());
		}
		// List<AthleteGroupScheduleDto> athleteGroupScheduleDto =
		// athleteGroupScheduleService.findByAthlete(dto.getId());
		// if (athleteGroupScheduleDto != null && athleteGroupScheduleDto.size()
		// > 0) {
		// dto.setGroupId(athleteGroupScheduleDto.get(0).getGroupId());
		// dto.setScheduleIds(athleteGroupScheduleDto.stream().map(AthleteGroupScheduleDto::getScheduleId)
		// .collect(Collectors.toSet()));
		// }

		return dto;
	}

	private AthleteDao convertToDao(AthleteDto dto) {
		if (dto == null) {
			return null;
		}
		AthleteDao dao = new ModelMapper().map(dto, AthleteDao.class);
		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}
		if (dto.getFamilyId() != 0) {
			dao.setFamily(familyRepository.findById(dto.getFamilyId()).get());
		}

		athleteGroupScheduleService.deleteByAthlete(dto.getId());

		if (dto.getScheduleIds() != null) {
			dto.getScheduleIds().stream().map(schId -> {

				AthleteGroupScheduleDto agsDto = new AthleteGroupScheduleDto();

				agsDto.setAthleteId(dto.getId());
				agsDto.setGroupId(dto.getGroupId());
				agsDto.setScheduleId(schId);
				return athleteGroupScheduleService.save(agsDto);
			}).collect(Collectors.toList());
		}

		return dao;
	}

	public List<GroupDto> findGroupsById(int id) {
		return athleteGroupScheduleService.findByAthlete(id).stream().map(AthleteGroupScheduleDto::getGroup)
				.collect(Collectors.toList());
	}
}
