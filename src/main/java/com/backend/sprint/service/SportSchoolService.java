package com.backend.sprint.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.SelectOptionDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.repository.SportSchoolRepository;

@Service
@Transactional
public class SportSchoolService {

	@Autowired
	private SportSchoolRepository repository;

	public Page<SportSchoolDto> findPagintation(Specification<SportSchoolDao> specification, Pageable pageable) {
		Page<SportSchoolDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<SportSchoolDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<SportSchoolDao> findAllDao() {
		Iterable<SportSchoolDao> findAll = repository.findAll();
		return StreamSupport.stream(findAll.spliterator(), false).collect(Collectors.toList());
	}

	public List<SelectOptionDto> findFeeTypesById(int sportSchoolId) {

		List<SelectOptionDto> options = new ArrayList<SelectOptionDto>();
		if (sportSchoolId == 1) {
			// Santa Ana
			options.add(new SelectOptionDto("CLUB", "Club"));
			options.add(new SelectOptionDto("CLUB_ANUAL", "Club Anual"));
			options.add(new SelectOptionDto("PISTAS", "Pistas"));
			options.add(new SelectOptionDto("LIC_PISTAS", "Licencia/Pistas"));
			options.add(new SelectOptionDto("ENTRENADOR", "Entrenadores"));
		} else if (sportSchoolId == 2) {

			// Paracuellos
			options.add(new SelectOptionDto("CLUB", "Club"));
			options.add(new SelectOptionDto("CLUB_ANUAL", "Club Anual"));
		} else if (sportSchoolId == 3) {

			// Externos
			options.add(new SelectOptionDto("LICENCIA", "Licencia"));
			options.add(new SelectOptionDto("SOCIO", "Socio"));
			options.add(new SelectOptionDto("ENTRENADOR", "Entrenadores"));
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
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private SportSchoolDto convertToDto(SportSchoolDao dao) {
		if (dao == null) {
			return null;
		}
		return new ModelMapper().map(dao, SportSchoolDto.class);
	}

	private SportSchoolDao convertToDao(SportSchoolDto dto) {
		if (dto == null) {
			return null;
		}
		return new ModelMapper().map(dto, SportSchoolDao.class);
	}

}
