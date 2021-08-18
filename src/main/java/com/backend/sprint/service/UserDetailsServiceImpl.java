package com.backend.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.sprint.beans.UserDetailsBean;
import com.backend.sprint.model.dto.UserDto;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto user = userService.findByUsername(username);

		if (user != null) {

			return new UserDetailsBean();
		} else {
			throw new UsernameNotFoundException("User Name is not Found");
		}
	}

}
