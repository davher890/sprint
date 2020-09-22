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
import com.backend.sprint.model.dao.AthleteGroupScheduleDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.repository.AthleteGroupScheduleRepository;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
public class AthleteService {

	@Autowired
	private AthleteRepository repository;

	@Autowired
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@Autowired
	private AthleteGroupScheduleRepository athleteGroupScheduleRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	// @Autowired
	// private FamilyRepository familyRepository;

	public Page<AthleteDto> findPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<AthleteDto> findByGroupAndSchedule(Specification<AthleteGroupScheduleDao> specification,
			Pageable pageable) {
		Page<AthleteGroupScheduleDao> daoPage = athleteGroupScheduleRepository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao.getId().getAthlete()));
	}

	public List<AthleteDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public AthleteDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public AthleteDto save(AthleteDto dto) {
		AthleteDao dao = repository.save(convertToDao(dto));

		if (dto.getScheduleIds() != null) {
			dto.getScheduleIds().stream().map(schId -> {

				AthleteGroupScheduleDto agsDto = new AthleteGroupScheduleDto();

				agsDto.setAthleteId(dao.getId());
				agsDto.setGroupId(dto.getGroupId());
				agsDto.setScheduleId(schId);
				return athleteGroupScheduleService.save(agsDto);
			}).collect(Collectors.toList());
		}

		return convertToDto(dao);
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

		List<AthleteGroupScheduleDto> athleteGroupSchedules = athleteGroupScheduleService.findByAthlete(dto.getId());
		if (athleteGroupSchedules.size() > 0) {
			dto.setGroupId(athleteGroupSchedules.get(0).getGroupId());
			dto.setScheduleIds(athleteGroupSchedules.parallelStream().map(d -> {
				return d.getScheduleId();
			}).collect(Collectors.toSet()));
		}
		// FamilyDao family = dao.getFamily();
		// if (family != null) {
		// dto.setFamilyId(family.getId());
		//
		// dto.setFamiliarOneFirstSurname(family.getFamiliarOneSurname());
		// dto.setFamiliarOneDni(family.getFamiliarOneDni());
		// dto.setFamiliarOneMail(family.getFamiliarOneMail());
		//
		// dto.setFamiliarTwoFirstSurname(family.getFamiliarTwoSurname());
		// dto.setFamiliarTwoDni(family.getFamiliarTwoDni());
		// dto.setFamiliarTwoMail(family.getFamiliarTwoMail());
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

		// List<FamilyDao> families =
		// familyRepository.findBySurnames(dto.getFirstSurname(),
		// dto.getSecondSurname());
		// // Create new family
		// if (families.size() == 0) {
		// FamilyDao family = new FamilyDao();
		// family.setFamiliarOneSurname(dto.getFamiliarOneFirstSurname());
		// family.setFamiliarOneDni(dto.getFamiliarOneDni());
		// family.setFamiliarOneMail(dto.getFamiliarOneMail());
		//
		// family.setFamiliarTwoSurname(dto.getFamiliarTwoFirstSurname());
		// family.setFamiliarTwoDni(dto.getFamiliarTwoDni());
		// family.setFamiliarTwoMail(dto.getFamiliarTwoMail());
		//
		// family.setCode(familyRepository.findLastCode() + 1);
		// family = familyRepository.save(family);
		// dao.setFamily(family);
		// }
		// // Assign family
		// else if (families.size() == 1) {
		// dao.setFamily(families.get(0));
		// } else { // (families.size() > 1)
		// // Calculate family
		// }

		if (dto.getFamilyCode() == 0) {
			Set<AthleteDao> relatives = getAthleteFamily(dto);
			if (relatives.size() > 0) {
				dto.setFamilyCode(relatives.iterator().next().getFamilyCode());
				dto.setCode(Long.valueOf(dto.getFamilyCode() + "" + relatives.size()));
			} else {
				long findLastFamilyCode = repository.findLastFamilyCode();
				dto.setFamilyCode(findLastFamilyCode + 1);
				dto.setCode(Long.valueOf(dto.getFamilyCode() + "" + 1));
			}
		}

		athleteGroupScheduleService.deleteByAthlete(dto.getId());

		return dao;
	}

	private Set<AthleteDao> getAthleteFamily(AthleteDto dto) {
		return repository.findRelatives(dto.getFirstSurname(), dto.getSecondSurname(), dto.getPhone1(), dto.getPhone2(),
				dto.getPhone3());
	}

	public List<GroupDto> findGroupsById(int id) {
		return athleteGroupScheduleService.findByAthlete(id).stream().map(AthleteGroupScheduleDto::getGroup)
				.collect(Collectors.toList());
	}
}
