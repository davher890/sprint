package com.backend.sprint.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

	public List<FeeTypeDto> findAll() {
		return StreamSupport.stream(repository.findAll(Sort.by(Direction.ASC, "name")).spliterator(), false)
				.filter(d -> d.getName() != null).map(this::convertToDto).collect(Collectors.toList());
	}

	public List<FeeTypeDto> findBySportSchoolId(long sportSchoolId) {
		return repository.findBySportSchoolId(sportSchoolId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
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
