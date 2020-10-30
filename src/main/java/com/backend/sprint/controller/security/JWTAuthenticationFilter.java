package com.backend.sprint.controller.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			UserDto creds = new ObjectMapper().readValue(request.getInputStream(), UserDto.class);

			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) {

		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		String token = Jwts.builder().setSubject(userDetails.getUsername())
				.setExpiration(new Date((new Date()).getTime() + 600000))
				.signWith(SignatureAlgorithm.HS512, SecurityUtils.SECRET).compact();

		response.addHeader(SecurityUtils.HEADER, SecurityUtils.PREFIX + " " + token);
		response.addHeader("Access-Control-Expose-Headers", SecurityUtils.HEADER);
	}
}
