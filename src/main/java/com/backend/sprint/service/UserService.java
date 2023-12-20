package com.backend.sprint.service;

import java.util.HashMap;
import java.util.HashSet;
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

import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.repository.GroupRepository;
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

	@Autowired
	private GroupRepository groupRepository;

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
			dto.setSportSchoolIds(new HashSet<Long>());
			dto.setSportSchoolNames(new HashSet<String>());
			dto.setSportSchoolMap(new HashMap<Long, String>());

			dao.getSportSchools().stream().forEach(d -> {
				dto.getSportSchoolMap().put(d.getId(), d.getName());
				dto.getSportSchoolIds().add(d.getId());
				dto.getSportSchoolNames().add(d.getName());
			});

			dto.setFeeTypeIds(new HashSet<Long>());
			dto.setFeeTypeNames(new HashSet<String>());
			dto.setFeeTypeMap(new HashMap<Long, String>());

			dao.getSportSchools().stream().forEach(d -> {
				d.getFeeTypes().stream().forEach(ft -> {
					dto.getFeeTypeMap().put(ft.getId(), ft.getName());
					dto.getFeeTypeIds().add(ft.getId());
					dto.getFeeTypeNames().add(ft.getName());
				});
			});
		}
		if (dao.getGroups() != null) {
			dto.setGroupIds(new HashSet<Long>());
			dto.setGroupNames(new HashSet<String>());
			dto.setGroupMap(new HashMap<Long, String>());

			dao.getGroups().stream().forEach(d -> {
				dto.getGroupMap().put(d.getId(), d.getName());
				dto.getGroupIds().add(d.getId());
				dto.getGroupNames().add(d.getName());
			});
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

//		if (dto.getUpdatedById() != 0) {
//			dao.setUpdatedBy(repository.findById(dto.getUpdatedById()).get());
//		}
		if (dto.getSportSchoolIds() != null) {
			dao.setSportSchools(dto.getSportSchoolIds().stream().map(sportSchoolId -> {
				return sportSchoolRepository.findById(sportSchoolId).get();
			}).collect(Collectors.toSet()));
		}
		if (dto.getGroupIds() != null) {
			dao.setGroups(dto.getGroupIds().stream().map(groupId -> {
				return groupRepository.findById(groupId).get();
			}).collect(Collectors.toSet()));
		}

		return dao;
	}

	public UserDto findByEmail(String email) {
		return convertToDto(repository.findByEmail(email));
	}

	public List<UserDto> findAdminUsers() {
		return repository.findAdminUsers().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<Long> findManagerUserUserIds(long id) {
		return repository.findManagerUserUserIds(id);
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
