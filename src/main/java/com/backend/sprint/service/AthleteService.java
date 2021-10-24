package com.backend.sprint.service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.FamilyDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.FamilyRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.utils.HistoricType;

@Service
@Transactional
public class AthleteService {

	@Autowired
	private AthleteRepository repository;

	@Autowired
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private HistoricService historicService;

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private FamilyService familyService;

	public Page<AthleteDto> findPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<AthleteDto> findByGroupAndScheduleTable(long groupId, List<Long> scheduleIds,
			Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findByGroupAndSchedulesPagination(groupId, scheduleIds, specification,
				pageable);
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

	public List<AthleteDao> findAllExcel() {
		Iterable<AthleteDao> findAll = repository.findAll();
		return StreamSupport.stream(findAll.spliterator(), false).collect(Collectors.toList());
	}

	public List<AthleteDao> findAllHistoricExcel() {
		return repository.findAllHistoric();
	}

	public AthleteDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public AthleteDto save(AthleteDto dto) throws DataIntegrityViolationException {

		if (dto.getBirthDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dto.getBirthDate());

			// New record, check if someone similar exists.
			if (dto.getId() == 0) {

				List<AthleteDao> findByUniqueColumns = repository.findByUniqueColumns(dto.getName(),
						dto.getFirstSurname(), dto.getSecondSurname(), calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
				if (findByUniqueColumns != null && findByUniqueColumns.size() > 0) {
					throw new DataIntegrityViolationException(null);
				}
			}
			// Old record, check if another distinct record exists
			else {
				List<AthleteDao> findByUniqueColumns = repository.findByIdAndUniqueColumns(dto.getId(), dto.getName(),
						dto.getFirstSurname(), dto.getSecondSurname(), calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
				if (findByUniqueColumns != null && findByUniqueColumns.size() > 0) {
					throw new DataIntegrityViolationException(null);
				}
			}
		}

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

	public AthleteDto findByUniqueColumns(String name, String firstSurname, String secondSurname, Date birthDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);

		Set<AthleteDto> collect = repository
				.findByUniqueColumns(name, firstSurname, secondSurname, calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
				.stream().map(this::convertToDto).collect(Collectors.toSet());
		if (collect == null || collect.isEmpty()) {
			return null;
		} else {
			if (collect.size() > 0) {
				return collect.iterator().next();
			} else {
				return null;
			}
		}
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

	public List<GroupDto> findGroupsById(int id) {
		return athleteGroupScheduleService.findByAthlete(id).stream().map(d -> groupService.findById(d.getGroupId()))
				.collect(Collectors.toList());
	}

	public void updateAthletesRegisterDate() {

		List<AthleteDto> athletes = this.findAll();
		athletes.stream().forEach(athlete -> {
			try {
				updateAthleteRegistrationDate(athlete);
			} catch (DataIntegrityViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	public void updateAthleteRegistrationDate(AthleteDto athlete) throws DataIntegrityViolationException {
		List<HistoricDto> athleteHistoric = historicService.findAthleteRegistration(athlete.getId());

		if (athleteHistoric != null && athleteHistoric.size() > 0) {
			HistoricDto lastRegisterDate = athleteHistoric.stream()
					.filter(p -> p.getType().equals(HistoricType.REGISTER.name()))
					.filter(p -> p.getDate().compareTo(new Date()) <= 1).findFirst().orElse(null);

			HistoricDto lastUnregisterDate = athleteHistoric.stream().filter(p -> p.getDate().before(new Date()))
					.filter(p -> p.getType().equals(HistoricType.UNREGISTER.name())).findFirst().orElse(null);

			if (lastRegisterDate != null) {
				athlete.setLastRegisterDate(lastRegisterDate.getDate());
			}
			if (lastUnregisterDate != null) {
				athlete.setLastUnregisterDate(lastUnregisterDate.getDate());
			}
			save(athlete);
		}
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
			dto.setSportSchoolId(dao.getSportSchool().getId());
		}

		List<AthleteGroupScheduleDto> athleteGroupSchedules = athleteGroupScheduleService.findByAthlete(dto.getId());
		if (athleteGroupSchedules.size() > 0) {
			dto.setGroupId(athleteGroupSchedules.get(0).getGroupId());
			dto.setScheduleIds(athleteGroupSchedules.parallelStream().map(d -> {
				return d.getScheduleId();
			}).collect(Collectors.toSet()));
			dto.setSchedules(athleteGroupSchedules.parallelStream().map(d -> {
				return scheduleService.findById(d.getScheduleId());
			}).collect(Collectors.toSet()));
		}

		FamilyDao family = dao.getFamily();
		if (family != null) {
			dto.setFamilyId(family.getId());
			dto.setFamilyCode(family.getCode());
		}

		if (dto.getHistoric() != null && !dto.getHistoric().isEmpty()) {
			List<HistoricDto> sortedHistoric = dto.getHistoric().stream().filter(p -> p.getDate().before(new Date()))
					.sorted(Comparator.comparing(HistoricDto::getDate).thenComparing(HistoricDto::getId).reversed())
					.collect(Collectors.toList());

			dto.setRegistered(
					sortedHistoric.isEmpty() || sortedHistoric.get(0).getType().equals(HistoricType.REGISTER.name()));
		} else {
			dto.setRegistered(true);
		}
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

		if (dto.getFamilyId() != 0) {
			dao.setFamily(familyRepository.findById(dto.getFamilyId()).get());
		}

		if (dto.getCode() == 0) {
			FamilyDto family = familyService.findById(dao.getFamily().getId());
			dao.setCode(Long.valueOf(family.getCode() + "" + (family.getAthleteIds().size() + 1)));
		}

		athleteGroupScheduleService.deleteByAthlete(dto.getId());

		return dao;
	}
}
