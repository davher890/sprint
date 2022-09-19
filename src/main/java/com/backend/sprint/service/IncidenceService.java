package com.backend.sprint.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.backend.sprint.model.dao.IncidenceDao;
import com.backend.sprint.model.dto.IncidenceDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.IncidenceRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class IncidenceService {

	@Autowired
	private IncidenceRepository repository;

	@Autowired
	private UserRepository userRepository;

	public Page<IncidenceDto> findPagintation(Specification<IncidenceDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<IncidenceDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public IncidenceDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	@Transactional
	public IncidenceDto save(IncidenceDto dto) throws DataIntegrityViolationException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			UserDto user = (UserDto) auth.getPrincipal();
			dto.setCreatedById(user.getId());
		}

		return convertToDto(repository.save(convertToDao(dto)));
	}

	private IncidenceDto convertToDto(IncidenceDao dao) {
		if (dao == null) {
			return null;
		}
		IncidenceDto dto = new ModelMapper().map(dao, IncidenceDto.class);

		if (dao.getCreatedBy() != null) {
			dto.setCreatedById(dao.getCreatedBy().getId());
		}

		return dto;
	}

	private IncidenceDao convertToDao(IncidenceDto dto) {
		IncidenceDao dao = new ModelMapper().map(dto, IncidenceDao.class);
		if (dto.getCreatedById() != 0) {
			dao.setCreatedBy(userRepository.findById(dto.getCreatedById()).get());
		} else {
			dao.setCreatedBy(null);
		}

		return dao;
	}

}
