package com.backend.sprint.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.sprint.controller.security.SecurityConstants;

public class JwtUtils {

	public static String generateJwtToken(String username, long userId) {

		String token = JWT.create().withSubject(username)
				.withExpiresAt(new Date((new Date()).getTime() + SecurityConstants.EXPIRATION_TIME))
				.withClaim("user_id", userId).sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

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
