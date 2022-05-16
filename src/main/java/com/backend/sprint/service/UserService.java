package com.backend.sprint.service;

import java.util.List;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	public Page<UserDto> findPagintation(Specification<UserDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<UserDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
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

		if (dao.getSportSchools() != null) {
			dto.setSportSchoolIds(
					dao.getSportSchools().stream().map(SportSchoolDao::getId).collect(Collectors.toSet()));
		}
		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		return dto;
	}

	private UserDao convertToDao(UserDto dto) {
		UserDao dao = new ModelMapper().map(dto, UserDao.class);

		if (dto.getId() == 0) {
			dao.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(repository.findById(dto.getUpdatedById()).get());
		}
		if (dto.getSportSchoolIds() != null) {
			dao.setSportSchools(dto.getSportSchoolIds().stream().map(sportSchoolId -> {
				return sportSchoolRepository.findById(sportSchoolId).get();
			}).collect(Collectors.toSet()));
		}

		return dao;
	}

	public UserDto findByUsername(String username) {
		return convertToDto(repository.findByUsername(username).get());
	}

	public UserDto findByEmail(String email) {
		return convertToDto(repository.findByEmail(email).get());
	}

	public UserDto changePassword(UserDto dto) {

		String newPassword = dto.getNewPassword();
		String oldPassword = dto.getOldPassword();
		

		if (passwordEncoder.matches(oldPassword, dto.getPassword())) {
			dto.setPassword(passwordEncoder.encode(newPassword));
		}

		return this.save(dto);
	}

}
