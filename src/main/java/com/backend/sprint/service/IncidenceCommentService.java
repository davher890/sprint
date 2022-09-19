package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.IncidenceCommentDao;
import com.backend.sprint.model.dto.IncidenceCommentDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.IncidenceCommentRepository;
import com.backend.sprint.repository.IncidenceRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class IncidenceCommentService {

	@Autowired
	private IncidenceCommentRepository repository;

	@Autowired
	private IncidenceRepository incidenceRepository;

	@Autowired
	private UserRepository userRepository;

	public Page<IncidenceCommentDto> findPagintation(Specification<IncidenceCommentDao> specification,
			Pageable pageable) {
		Page<IncidenceCommentDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<IncidenceCommentDto> findIncidenceComments(long id) {
		return repository.findIncidenceComments(id).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional
	public IncidenceCommentDto save(IncidenceCommentDto dto) throws DataIntegrityViolationException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			UserDto user = (UserDto) auth.getPrincipal();
			dto.setCreatedById(user.getId());
		}

		return convertToDto(repository.save(convertToDao(dto)));
	}

	private IncidenceCommentDto convertToDto(IncidenceCommentDao dao) {
		if (dao == null) {
			return null;
		}
		IncidenceCommentDto dto = new ModelMapper().map(dao, IncidenceCommentDto.class);
		dto.setIncidenceId(dao.getIncidence().getId());

		if (dao.getCreatedBy() != null) {
			dto.setCreatedById(dao.getCreatedBy().getId());
			dto.setCreatedByName(dao.getCreatedBy().getUsername());
		}

		return dto;
	}

	private IncidenceCommentDao convertToDao(IncidenceCommentDto dto) {
		IncidenceCommentDao dao = new ModelMapper().map(dto, IncidenceCommentDao.class);
		if (dto.getIncidenceId() != 0) {
			dao.setIncidence(incidenceRepository.findById(dto.getIncidenceId()).get());
		}

		if (dto.getCreatedById() != 0) {
			dao.setCreatedBy(userRepository.findById(dto.getCreatedById()).get());
		} else {
			dao.setCreatedBy(null);
		}

		return dao;
	}

}
