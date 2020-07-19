package com.backend.sprint.model.dto;

import org.apache.poi.ss.usermodel.CellType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelValueDto {

	private String value;

	private CellType type;

	public ExcelValueDto(String value, CellType type) {
		this.value = value;
		this.type = type;
	}
}
