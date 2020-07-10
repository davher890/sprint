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

import com.backend.sprint.model.dao.TrainerDao;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.repository.TrainerRepository;

@Service
public class TrainerService {

	@Autowired
	private TrainerRepository repository;

	@Autowired
	private GroupService groupService;

	public Page<TrainerDto> findPagintation(Specification<TrainerDao> specification, Pageable pageable) {
		Page<TrainerDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<TrainerDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public TrainerDto findById(long id) {
		TrainerDao dao = repository.findById(id).orElse(null);
		return convertToDto(dao);
	}

	public TrainerDto save(TrainerDto dto) {
		TrainerDao dao = convertToDao(dto);
		return convertToDto(repository.save(dao));
	}

	private TrainerDto convertToDto(TrainerDao dao) {
		if (dao == null) {
			return null;
		}

		TrainerDto dto = new ModelMapper().map(dao, TrainerDto.class);
		Set<Long> groupIds = groupService.findByTrainerId(dao.getId()).stream().map(GroupDto::getId)
				.collect(Collectors.toSet());
		dto.setGroupIds(groupIds);
		return dto;
	}

	private TrainerDao convertToDao(TrainerDto dto) {
		if (dto == null) {
			return null;
		}
		return new ModelMapper().map(dto, TrainerDao.class);
	}

}
