package com.backend.sprint.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.SelectOptionDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.repository.UserRepository;
import com.backend.sprint.utils.FeeTypeEnum;

@Service
@Transactional
public class SportSchoolService {

	@Autowired
	private SportSchoolRepository repository;

	@Autowired
	private UserRepository userRepository;

	public Page<SportSchoolDto> findPagintation(Specification<SportSchoolDao> specification, Pageable pageable,
			String sort, Direction sortDirection) {
		Page<SportSchoolDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<SportSchoolDto> findAll() {
		return StreamSupport.stream(repository.findAll(Sort.by(Direction.ASC, "name")).spliterator(), false)
				.filter(d -> d.getName() != null).map(this::convertToDto).collect(Collectors.toList());
	}

	public List<SportSchoolDto> findAllById(Set<Long> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false).filter(d -> d.getName() != null)
				.map(this::convertToDto).sorted(Comparator.comparing(SportSchoolDto::getName))
				.collect(Collectors.toList());
	}

	public List<SportSchoolDao> findAllByIdDao(Set<Long> ids) {
		Iterable<SportSchoolDao> findAll = repository.findAllById(ids);
		return StreamSupport.stream(findAll.spliterator(), false).collect(Collectors.toList());
	}

	public List<SelectOptionDto> findFeeTypesById(long sportSchoolId) {

		List<SelectOptionDto> options = new ArrayList<SelectOptionDto>();
		if (sportSchoolId == 1) {
			// Santa Ana
			options.add(new SelectOptionDto(FeeTypeEnum.SOCIO.name(), FeeTypeEnum.SOCIO.label));
			options.add(new SelectOptionDto(FeeTypeEnum.CLUB.name(), FeeTypeEnum.CLUB.label));
			options.add(new SelectOptionDto(FeeTypeEnum.CLUB_ANUAL.name(), FeeTypeEnum.CLUB_ANUAL.label));
		} else if (sportSchoolId == 2) {

			// Paracuellos
			options.add(new SelectOptionDto(FeeTypeEnum.SOCIO.name(), FeeTypeEnum.SOCIO.label));
			options.add(new SelectOptionDto(FeeTypeEnum.CLUB.name(), FeeTypeEnum.CLUB.label));
			options.add(new SelectOptionDto(FeeTypeEnum.CLUB_ANUAL.name(), FeeTypeEnum.CLUB_ANUAL.label));
		} else if (sportSchoolId == 3) {

			// Externos
			options.add(new SelectOptionDto(FeeTypeEnum.SOCIO.name(), FeeTypeEnum.SOCIO.label));
			options.add(new SelectOptionDto(FeeTypeEnum.FED.name(), FeeTypeEnum.FED.label));
		} else if (sportSchoolId == 5) {

			// Padres
			options.add(new SelectOptionDto(FeeTypeEnum.SOCIO.name(), FeeTypeEnum.SOCIO.label));
			options.add(new SelectOptionDto(FeeTypeEnum.PISTAS.name(), FeeTypeEnum.PISTAS.label));
			options.add(new SelectOptionDto(FeeTypeEnum.PISTAS_FED.name(), FeeTypeEnum.PISTAS_FED.label));

		} else {
			options.add(new SelectOptionDto(FeeTypeEnum.CLUB.name(), FeeTypeEnum.CLUB.label));
		}

		return options;
	}

	public List<SelectOptionDto> findCollectionsById(long sportSchoolId) {

		List<SelectOptionDto> options = new ArrayList<SelectOptionDto>();
		if (sportSchoolId == 1 || sportSchoolId == 2) {
			// Santa Ana y Paracuellos
			options.add(new SelectOptionDto("initial_fee", "Cuota inicial"));
			options.add(new SelectOptionDto("quarterly_fee", "Cuota trimestral"));
		} else if (sportSchoolId == 3) {
			// Externos
			options.add(new SelectOptionDto("external_fee", "Cuota externos"));
		} else if (sportSchoolId == 3 || sportSchoolId == 5) {
			// Padres
			options.add(new SelectOptionDto("parents_fee", "Cuota padres"));
		}

		return options;
	}

	public SportSchoolDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public SportSchoolDto findByName(String name) {
		return convertToDto(repository.findByName(name));
	}

	public SportSchoolDto save(SportSchoolDto dto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		dto.setUpdatedById(user.getId());

		return convertToDto(repository.save(convertToDao(dto)));
	}

	public SportSchoolDto deleteById(long id) {

		SportSchoolDto sportSchool = this.findById(id);
		sportSchool.setActive(false);
		return this.save(sportSchool);
	}

	private SportSchoolDto convertToDto(SportSchoolDao dao) {
		if (dao == null) {
			return null;
		}
		SportSchoolDto dto = new ModelMapper().map(dao, SportSchoolDto.class);

		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		} else {
			dao.setUpdatedBy(null);
		}
		return dto;
	}

	private SportSchoolDao convertToDao(SportSchoolDto dto) {
		if (dto == null) {
			return null;
		}
		SportSchoolDao dao = new ModelMapper().map(dto, SportSchoolDao.class);

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}

		return dao;
	}

}
