package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

	private long id;

	private String username;

	private String mail;

	private String password;

	private String token;

}
