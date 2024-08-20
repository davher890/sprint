package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

	private long id;

	private Set<Long> sportSchoolIds;

	private Set<String> sportSchoolNames;

	private Map<Long, String> sportSchoolMap;

	private Set<Long> groupIds;

	private Set<String> groupNames;

	private Map<Long, String> groupMap;

	private Set<Long> feeTypeIds;

	private Set<String> feeTypeNames;

	private Map<Long, String> feeTypeMap;

	private String username;

	private String email;

	private String password;

	private String oldPassword;

	private String newPassword;

	private String token;

	private String stripeId;

	private boolean isAdmin;

	private boolean isManager;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private long updatedById;

}
