package com.backend.sprint.controller.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.utils.JwtUtils;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		String header = request.getHeader(SecurityConstants.HEADER_STRING);

		if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);

	}

	// Reads the JWT from the Authorization header, and then uses JWT to
	// validate the token
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
			HttpServletResponse response) {
		String encodedJwtToken = JwtUtils.getJwtToken(request);

		if (encodedJwtToken != null) {
			// parse the token.
			DecodedJWT decodedJwtToken = JwtUtils.getJwtTokenClaims(encodedJwtToken);
			String userStr = decodedJwtToken.getSubject();
			Long[] sportSchoolIds = decodedJwtToken.getClaim(SecurityConstants.SPORT_SCHOOL_IDS).asArray(Long.class);

			if (userStr != null) {
				Long userId = decodedJwtToken.getClaim(SecurityConstants.USER_ID).asLong();

				UserDto user = new UserDto();
				user.setUsername(userStr);
				user.setId(userId);
				user.setSportSchoolIds(new HashSet<Long>(Arrays.asList(sportSchoolIds)));

				String token = JwtUtils.generateJwtToken(user);
				response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + " " + token);

				// new arraylist means authorities
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
						new ArrayList<>());
				authentication.setDetails(user);
				return authentication;
			}
			return null;
		}
		return null;
	}
}
