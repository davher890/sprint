package com.backend.sprint.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.sprint.controller.security.SecurityConstants;
import com.backend.sprint.model.dto.UserDto;

public class JwtUtils {

	public static String generateJwtToken(UserDto userDto) {

		Set<Long> sportShoolIds = userDto.getSportSchoolIds();
		if (sportShoolIds == null) {
			sportShoolIds = new HashSet<Long>();
		}
		String token = JWT.create().withSubject(userDto.getUsername())
				.withExpiresAt(new Date((new Date()).getTime() + SecurityConstants.EXPIRATION_TIME))
				.withClaim(SecurityConstants.USER_ID, userDto.getId())
				.withArrayClaim(SecurityConstants.SPORT_SCHOOL_IDS,
						sportShoolIds.toArray(new Long[sportShoolIds.size()]))
				.sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
		return token;
	}

	public static String getJwtToken(HttpServletRequest request) {
		return request.getHeader(SecurityConstants.HEADER_STRING);
	}

	public static DecodedJWT getJwtTokenClaims(String jwtToken) {
		return JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
				.verify(jwtToken.replace(SecurityConstants.TOKEN_PREFIX, "").trim());
	}
}
