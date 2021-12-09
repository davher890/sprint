package com.backend.sprint.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.sprint.beans.UserDetailsBean;
import com.backend.sprint.model.dto.UserDto;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto user = userService.findByUsername(username);

		if (user != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
//			return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
			return new UserDetailsBean(user, new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User Name is not Found");
		}
	}

}
