package com.backend.sprint.controller.security;

public class SecurityConstants {

	public static final String SECRET = "SECRET_KEY";
	public static final long EXPIRATION_TIME = 18000_000; // 300 mins = 5 hours
	public static final String TOKEN_PREFIX = "Bearer";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/api/services/controller/user";

	public static final String USER_ID = "user_id";
	public static final String SPORT_SCHOOL_IDS = "sport_school_ids";
}