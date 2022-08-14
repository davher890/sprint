package com.backend.sprint.service;

import java.util.Calendar;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.FamilyRepository;
import com.backend.sprint.repository.GroupRepository;
import com.backend.sprint.repository.ScheduleRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.repository.UserRepository;
import com.backend.sprint.specifications.AthleteSpecificationConstructor;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.HistoricType;

@Service
@Transactional
public class AthleteService {

	@Autowired
	private AthleteRepository repository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private HistoricService historicService;

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FamilyService familyService;

	public Page<AthleteDto> findPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<AthleteDao> findDaoPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	public Page<AthleteDto> findByGroupAndScheduleTable(long groupId, List<Long> scheduleIds,
			AthleteSpecificationConstructor<AthleteDao> specification, Pageable pageable) {

		Page<AthleteDao> daoPage = repository
				.findAll(specification.athleteGroupSchedulesSpecification(groupId, scheduleIds), pageable);

//		Page<AthleteDao> daoPage = repository.findByGroupAndSchedulesPagination(groupId, scheduleIds, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<AthleteDto> findByGroupAndSchedule(long groupId, List<Long> scheduleIds) {
		List<AthleteDao> daoPage = repository.findByGroupAndSchedules(groupId, scheduleIds);
		return daoPage.stream().map(dao -> convertToDto(dao)).collect(Collectors.toList());
	}

	public List<AthleteDto> findByRegistrationDates(Date startDate, Date endDate) {
		List<AthleteDao> daoPage = repository.findByRegistrationDates(startDate, endDate);
		return daoPage.stream().map(dao -> convertToDto(dao)).collect(Collectors.toList());
	}

	public List<AthleteDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<AthleteDto> findAllRegistered() {
		return StreamSupport.stream(repository.findAllRegistered().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public AthleteDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public AthleteDto save(AthleteDto dto) {

		if (dto.getBirthDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dto.getBirthDate());
		}

		AthleteDao dao = repository.save(convertToDao(dto));

		// New record, add first historic.
		if (dto.getId() == 0) {

			HistoricDto register = new HistoricDto();
			register.setAthleteId(dao.getId());
			register.setDate(new Date());
			register.setType(HistoricType.REGISTER.name());
			historicService.save(register);
		}
		return convertToDto(dao);
	}

	public Set<AthleteDto> findByFamily(long familyId) {
		return repository.findByFamily(familyId).stream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public AthleteDto findByCode(long code) {
		return convertToDto(repository.findByCode(code));
	}

	public List<AthleteDto> findByPhoneList(List<String> phoneList) {
		return convertToDto(repository.findByPhoneList(phoneList));
	}

	public List<AthleteDto> findBySurnames(String firstSurname, String secondSurname) {
		return convertToDto(repository.findBySurnames(firstSurname, secondSurname));
	}

	private FamilyDto newFamily() {
		FamilyDto family = new FamilyDto();
		family.setCode(familyService.findLastCode() + 1);
		return familyService.save(family);
	}

	public List<AthleteDto> findByFamilyCode(AthleteDto dto) {
		return convertToDto(repository.findByFamilyCode(dto.getFamilyCode()));
	}

	public List<AthleteDao> findDaoByGroupId(long id) {
		return repository.findByGroupId(id);
	}

	public List<AthleteDto> findByGroupId(long id) {
		return repository.findByGroupId(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public void deleteByGroupId(long id) {
		repository.deleteGroupAthletes(id);
	}

	public AthleteDto deleteById(long id) {

		AthleteDto athlete = this.findById(id);
		athlete.setActive(false);
		return this.save(athlete);
	}

	private List<AthleteDto> convertToDto(List<AthleteDao> dao) {
		return dao.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private AthleteDto convertToDto(AthleteDao dao) {
		if (dao == null) {
			return null;
		}

		AthleteDto dto = new ModelMapper().map(dao, AthleteDto.class);

		if (dao.getSportSchool() != null) {
			dto.setSportSchoolName(dao.getSportSchool().getName());
		}

		if (dao.getGroup() != null) {
			dto.setGroupName(dao.getGroup().getName());
		}

		if (dao.getFamily() != null) {
			dto.setFamilyCode(dao.getFamily().getCode());
		}

		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		if (dao.getSchedules() != null) {
			dto.setSchedules(
					dao.getSchedules().parallelStream().map(scheduleService::convertToDto).collect(Collectors.toSet()));
			dto.setScheduleIds(dao.getSchedules().parallelStream().map(ScheduleDao::getId).collect(Collectors.toSet()));
			dto.setNumDays(dto.getScheduleIds().size());
			dto.setDays(dao.getSchedules().parallelStream().map(ScheduleDao::getDay).collect(Collectors.joining(",")));
		}

		dto.setLastRegisterDate(AthleteUtils.getLastRegisterDate(dao));
		dto.setLastUnregisterDate(AthleteUtils.getLastUnregisterDate(dao));

		return dto;
	}

	private AthleteDao convertToDao(AthleteDto dto) {
		if (dto == null) {
			return null;
		}
		if (dto.getFamilyCode() > 0) {
			FamilyDto family = familyService.findByCode(dto.getFamilyCode());
			dto.setFamilyId(family.getId());
		}

		if (dto.getFamilyId() == 0) {
			FamilyDto family = newFamily();
			dto.setFamilyId(family.getId());
		}

		AthleteDao dao = new ModelMapper().map(dto, AthleteDao.class);

		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}

		if (dto.getGroupId() != 0) {
			dao.setGroup(groupRepository.findById(dto.getGroupId()).get());
		}

		if (dto.getFamilyId() != 0) {
			dao.setFamily(familyRepository.findById(dto.getFamilyId()).get());
		}

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}

		if (dto.getCode() == 0) {
			FamilyDto family = familyService.findById(dao.getFamily().getId());
			dao.setCode(Long.valueOf(family.getCode() + "" + (family.getAthleteIds().size() + 1)));
		}

		if (dto.getScheduleIds() != null) {
			dao.setSchedules(dto.getScheduleIds().stream().map(scheduleId -> {
				return scheduleRepository.findById(scheduleId).get();
			}).collect(Collectors.toSet()));
		}

		return dao;
	}
}
