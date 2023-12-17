package com.backend.sprint.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.FeeTypeDao;
import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.FeeTypeDto;
import com.backend.sprint.repository.FeeTypeRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class FeeTypeService {

	@Autowired
	private FeeTypeRepository repository;

	@Autowired
	private UserRepository userRepository;

	public FeeTypeDto save(FeeTypeDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public Set<FeeTypeDto> convertToDto(Set<FeeTypeDao> dao) {
		return dao.parallelStream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public FeeTypeDto convertToDto(FeeTypeDao dao) {
		if (dao == null) {
			return null;
		}
		FeeTypeDto dto = new ModelMapper().map(dao, FeeTypeDto.class);

		UserDao createdBy = dao.getCreatedBy();
		if (createdBy != null) {
			dto.setCreatedById(createdBy.getId());
		}
		UserDao updatedBy = dao.getUpdatedBy();
		if (updatedBy != null) {
			dto.setUpdatedById(updatedBy.getId());
		}

		return dto;
	}

	public FeeTypeDao convertToDao(FeeTypeDto dto) {
		if (dto == null) {
			return null;
		}

		FeeTypeDao dao = new ModelMapper().map(dto, FeeTypeDao.class);
		if (dto.getCreatedById() != 0) {
			dao.setCreatedBy(userRepository.findById(dto.getCreatedById()).get());
		}
		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}
		return dao;
	}

}
