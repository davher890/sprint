package com.backend.sprint.service;

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
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.TrainerDao;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.repository.TrainerRepository;
import com.backend.sprint.repository.UserRepository;

@Service
public class TrainerService {

	@Autowired
	private TrainerRepository repository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserRepository userRepository;

	public Page<TrainerDto> findPagintation(Specification<TrainerDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<TrainerDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<TrainerDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<TrainerDao> findAllDao() {
		Iterable<TrainerDao> findAll = repository.findAll();
		return StreamSupport.stream(findAll.spliterator(), false).collect(Collectors.toList());
	}

	public TrainerDto findById(long id) {
		TrainerDao dao = repository.findById(id).orElse(null);
		return convertToDto(dao);
	}

	public TrainerDto findByName(String name) {
		TrainerDao dao = repository.findByName(name);
		return convertToDto(dao);
	}

	public TrainerDto save(TrainerDto dto) {
		TrainerDao dao = convertToDao(dto);
		return convertToDto(repository.save(dao));
	}

	public TrainerDto deleteById(long id) {

		TrainerDto trainer = this.findById(id);
		trainer.setActive(false);
		return this.save(trainer);
	}

	private TrainerDto convertToDto(TrainerDao dao) {
		if (dao == null) {
			return null;
		}

		TrainerDto dto = new ModelMapper().map(dao, TrainerDto.class);
		Set<Long> groupIds = groupService.findByTrainerId(dao.getId()).stream().map(GroupDto::getId)
				.collect(Collectors.toSet());
		dto.setGroupIds(groupIds);

		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		return dto;
	}

	private TrainerDao convertToDao(TrainerDto dto) {
		if (dto == null) {
			return null;
		}
		TrainerDao dao = new ModelMapper().map(dto, TrainerDao.class);

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}

		return dao;
	}

}
