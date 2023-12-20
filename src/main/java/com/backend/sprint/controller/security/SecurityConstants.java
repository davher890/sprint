package com.backend.sprint.controller.security;

public class SecurityConstants {

	public static final String SECRET = "SECRET_KEY";
	public static final long EXPIRATION_TIME = 18000_000; // 300 mins = 5 hours
	public static final String TOKEN_PREFIX = "Bearer";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/api/services/controller/user";

	public static final String USER_ID = "user_id";
	public static final String IS_ADMIN = "is_admin";
	public static final String IS_MANAGER = "is_manager";
	public static final String SPORT_SCHOOL_IDS = "sport_school_ids";
	public static final String SPORT_SCHOOL_NAMES = "sport_school_names";
	public static final String SPORT_SCHOOL_MAP = "sport_school_map";
	public static final String GROUP_IDS = "group_ids";
	public static final String GROUP_NAMES = "group_names";
	public static final String GROUP_MAP = "group_map";
	public static final String FEE_TYPE_IDS = "fee_type_ids";
	public static final String FEE_TYPE_NAMES = "fee_type_names";
	public static final String FEE_TYPE_MAP = "fee_type_map";
}