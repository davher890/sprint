package com.backend.sprint.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SelectOptionDto {

	public SelectOptionDto(String id, String name) {
		this.id = id;
		this.name = name;
	}

	private String id;

	private String name;

}
