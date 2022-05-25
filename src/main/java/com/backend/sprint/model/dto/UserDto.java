package com.backend.sprint.model.dto;

import java.util.Date;
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

	private String username;

	private String email;

	private String password;

	private String oldPassword;

	private String newPassword;

	private String token;

	private boolean isAdmin;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private long updatedById;

}
