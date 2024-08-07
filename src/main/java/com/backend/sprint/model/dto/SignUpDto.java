package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignUpDto {

	private String username;

	private String email;

	private String password;

	private String confirmPassword;

}
