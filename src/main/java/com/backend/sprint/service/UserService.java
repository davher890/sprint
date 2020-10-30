package com.backend.sprint.service;

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

import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository repository;

	public Page<UserDto> findPagintation(Specification<UserDao> specification, Pageable pageable) {
		Page<UserDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<UserDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public UserDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public UserDto save(UserDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	private UserDto convertToDto(UserDao dao) {
		if (dao == null) {
			return null;
		}

		UserDto dto = new ModelMapper().map(dao, UserDto.class);
		return dto;
	}

	private UserDao convertToDao(UserDto dto) {
		UserDao dao = new ModelMapper().map(dto, UserDao.class);
		return dao;
	}

	public UserDto findByUsername(String username) {
		return convertToDto(repository.findByUsername(username).get());
	}

}
