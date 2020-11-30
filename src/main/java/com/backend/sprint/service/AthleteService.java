package com.backend.sprint.service;

import java.util.List;
import java.util.Objects;
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
import com.backend.sprint.model.dao.AthleteGroupScheduleDao;
import com.backend.sprint.model.dao.FamilyDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.repository.AthleteGroupScheduleRepository;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.repository.FamilyRepository;
import com.backend.sprint.repository.SportSchoolRepository;

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
	private AthleteGroupScheduleRepository athleteGroupScheduleRepository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private FamilyService familyService;

	public Page<AthleteDto> findPagintation(Specification<AthleteDao> specification, Pageable pageable) {
		Page<AthleteDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public Page<AthleteDto> findByGroupAndSchedule(Specification<AthleteGroupScheduleDao> specification,
			Pageable pageable) {
		Page<AthleteGroupScheduleDao> daoPage = athleteGroupScheduleRepository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(repository.findById(dao.getAthleteId()).get()));
	}

	public List<AthleteDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<AthleteDao> findAllExcel() {
		Iterable<AthleteDao> findAll = repository.findAll();
		return StreamSupport.stream(findAll.spliterator(), false).collect(Collectors.toList());
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

		return dto;
	}

	private AthleteDao convertToDao(AthleteDto dto) {
		if (dto == null) {
			return null;
		}
		AthleteDao dao = new ModelMapper().map(dto, AthleteDao.class);

		if (dto.getFamilyId() == 0) {
			List<AthleteDao> relatives = repository.findBySurnames(dto.getFirstSurname(), dto.getSecondSurname());
			// Assign family
			if (relatives != null && relatives.size() > 0) {
				// Calculate family
				relatives = relatives.parallelStream().map(d -> {
					if (dto.getPhone1().trim().equals(d.getPhone1().trim())
							|| dto.getPhone1().trim().equals(d.getPhone2().trim())
							|| dto.getPhone1().trim().equals(d.getPhone3().trim())) {
						return d;
					} else {
						return null;
					}
				}).filter(Objects::nonNull).collect(Collectors.toList());
				if (!relatives.isEmpty()) {
					dto.setFamilyId(relatives.get(0).getFamily().getId());
				}
			}
			// Create new family
			// if (relatives == null || relatives.isEmpty()) {
			else {
				FamilyDto family = new FamilyDto();
				family.setCode(familyService.findLastCode() + 1);
				family = familyService.save(family);
				dto.setFamilyId(family.getId());
			}

		}

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

	public List<AthleteDto> getRelatives(AthleteDto dto) {
		return convertToDto(repository.findRelatives(dto.getFirstSurname(), dto.getSecondSurname(), dto.getPhone1(),
				dto.getPhone2(), dto.getPhone3()));
	}

	public List<GroupDto> findGroupsById(int id) {
		return athleteGroupScheduleService.findByAthlete(id).stream().map(d -> groupService.findById(d.getGroupId()))
				.collect(Collectors.toList());
	}

	public FeeDto getFee(AthleteDto dto) {

		FeeDto fee = new FeeDto();
		if (dto.isSpecialization()) {
			fee.setMonthlyFee(100);
		} else {
			if (dto.getScheduleIds() != null) {
				switch (dto.getScheduleIds().size()) {
				case 1:
					fee.setMonthlyFee(50);
					break;
				case 2:
					fee.setMonthlyFee(85);
					break;
				case 3:
					fee.setMonthlyFee(100);
					break;
				default:
					if (dto.getScheduleIds().size() > 3) {
						fee.setMonthlyFee(100);
					} else {
						fee.setMonthlyFee(0);
					}
				}
			}
		}
		List<AthleteDto> relatives = this.getRelatives(dto);
		// if (dto.getLicenseType().equals("PISTAS") ||
		// dto.getLicenseType().equals("LICENCIA")) {
		if (relatives.size() > 1) {
			fee.setEnrollmentFee(20);
		} else {
			fee.setEnrollmentFee(40);
		}
		// A partir del tercer miembro solo se paga una cuota de socio por
		// familia
		fee.setMembershipFee(40);

		return fee;
	}
}
