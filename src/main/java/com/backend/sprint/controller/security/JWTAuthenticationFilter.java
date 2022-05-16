package com.backend.sprint.controller.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.sprint.beans.UserDetailsBean;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;

		setFilterProcessesUrl("/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			UserDto creds = new ObjectMapper().readValue(request.getInputStream(), UserDto.class);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					creds.getEmail(), creds.getPassword(), new ArrayList<>());
			return authenticationManager.authenticate(authentication);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) {

		UserDetailsBean userDetails = (UserDetailsBean) auth.getPrincipal();

		String token = JwtUtils.generateJwtToken(userDetails.getUser());
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + " " + token);
		response.addHeader("Access-Control-Expose-Headers", SecurityConstants.HEADER_STRING);
	}
}
