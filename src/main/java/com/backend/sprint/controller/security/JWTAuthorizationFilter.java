package com.backend.sprint.controller.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.utils.SecurityUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		String header = request.getHeader(SecurityUtils.HEADER);

		if (header == null || !header.startsWith(SecurityUtils.PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);

	}

	// Reads the JWT from the Authorization header, and then uses JWT to
	// validate the token
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = getJWTToken(request);

		if (token != null) {
			// parse the token.
			String userStr = getJWTTokenClaims(token).getSubject();

			if (userStr != null) {
				UserDto user = new UserDto();
				user.setUsername(userStr);
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

	// private void setUpSpringAuthentication(Claims claims) {
	// @SuppressWarnings("unchecked")
	// List<String> authorities = (List) claims.get("authorities");
	//
	// String username = claims.getSubject();
	// UserDetails userDetails =
	// userDetailsService.loadUserByUsername(username);
	// UsernamePasswordAuthenticationToken auth = new
	// UsernamePasswordAuthenticationToken(userDetails, null,
	// userDetails.getAuthorities());
	// auth.setDetails(new
	// WebAuthenticationDetailsSource().buildDetails(request));
	//
	// // UsernamePasswordAuthenticationToken auth = new
	// // UsernamePasswordAuthenticationToken(username, null,
	// //
	// authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	//
	// SecurityContextHolder.getContext().setAuthentication(auth);
	// }

	private String getJWTToken(HttpServletRequest request) {
		return request.getHeader(SecurityUtils.HEADER).replace(SecurityUtils.PREFIX, "");
	}

	private Claims getJWTTokenClaims(String jwtToken) {
		return Jwts.parser().setSigningKey(SecurityUtils.SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
	}

	private boolean hasJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(SecurityUtils.HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(SecurityUtils.PREFIX))
			return false;
		return true;
	}
}
