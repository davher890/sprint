package com.backend.sprint.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.GroupDao;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.repository.GroupRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository repository;

	public List<GroupDto> findAll() {
		return repository.findAll().stream().map(dao -> {
			return new ModelMapper().map(dao, GroupDto.class);
		}).collect(Collectors.toList());
	}

	public GroupDto findById(long id) {
		GroupDao dao = repository.findById(id).get();
		return new ModelMapper().map(dao, GroupDto.class);
	}

	public GroupDto save(GroupDto dto) {
		GroupDao dao = new ModelMapper().map(dto, GroupDao.class);
		return new ModelMapper().map(repository.save(dao), GroupDto.class);
	}

}
