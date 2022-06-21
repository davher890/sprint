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
		Set<Long> groupIds = userDto.getGroupIds();
		Set<String> sportShoolNames = userDto.getSportSchoolNames();
		Set<String> groupNames = userDto.getGroupNames();
		if (sportShoolIds == null) {
			sportShoolIds = new HashSet<Long>();
			sportShoolNames = new HashSet<String>();
		}
		if (groupIds == null) {
			groupIds = new HashSet<Long>();
			groupNames = new HashSet<String>();
		}
		String token = JWT.create().withSubject(userDto.getUsername())
				.withExpiresAt(new Date((new Date()).getTime() + SecurityConstants.EXPIRATION_TIME))
				.withClaim(SecurityConstants.USER_ID, userDto.getId())
				.withClaim(SecurityConstants.IS_ADMIN, userDto.isAdmin())
				.withClaim(SecurityConstants.IS_MANAGER, userDto.isManager())
				.withArrayClaim(SecurityConstants.SPORT_SCHOOL_IDS,
						sportShoolIds.toArray(new Long[sportShoolIds.size()]))
				.withArrayClaim(SecurityConstants.GROUP_IDS, groupIds.toArray(new Long[groupIds.size()]))
				.withArrayClaim(SecurityConstants.SPORT_SCHOOL_NAMES,
						sportShoolNames.toArray(new String[sportShoolNames.size()]))
				.withArrayClaim(SecurityConstants.GROUP_NAMES, groupNames.toArray(new String[groupNames.size()]))
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
