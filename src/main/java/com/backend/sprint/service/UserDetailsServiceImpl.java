package com.backend.sprint.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.UserDto;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserService userService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto user = userService.findByUsername(username);

		if (user != null) {

			return new UserDetails() {

				@Override
				public boolean isEnabled() {
					return true;
				}

				@Override
				public boolean isCredentialsNonExpired() {
					return true;
				}

				@Override
				public boolean isAccountNonLocked() {
					return true;
				}

				@Override
				public boolean isAccountNonExpired() {
					return true;
				}

				@Override
				public String getUsername() {
					return user.getUsername();
				}

				@Override
				public String getPassword() {
					return bCryptPasswordEncoder.encode(user.getPassword());
				}

				@Override
				public Collection<? extends GrantedAuthority> getAuthorities() {
					return new ArrayList<>();
				}
			};
		} else {
			throw new UsernameNotFoundException("User Name is not Found");
		}
	}

}
