package com.backend.sprint.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.sprint.beans.UserDetailsBean;
import com.backend.sprint.model.dto.UserDto;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
		UserDto user = userService.findByEmail(mail);

		if (user != null) {
			user.setPassword(user.getPassword());

			Set<SimpleGrantedAuthority> authorities = new HashSet<>();
			IntStream.range(0, 3).forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

			return new UserDetailsBean(user, authorities);
		} else {
			throw new UsernameNotFoundException("User Name is not Found");
		}
	}

}
