package com.backend.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.UserDao;
import com.backend.sprint.model.dto.UserStatisticsDto;
import com.backend.sprint.repository.UserRepository;

@Service
@Transactional
public class UserStatisticsService {

	@Autowired
	private UserRepository userRepository;

	public UserStatisticsDto findByUserId(long userId) {

		UserStatisticsDto dto = new UserStatisticsDto();

		UserDao user = userRepository.findById(userId).get();
		dto.setNumSportSchools(user.getSportSchools().size());
		dto.setNumGroups(user.getGroups().size());

		return dto;
	}

}
