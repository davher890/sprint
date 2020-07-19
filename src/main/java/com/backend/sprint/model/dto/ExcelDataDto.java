package com.backend.sprint.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelDataDto {

	private List<ExcelValueDto> data = new ArrayList<ExcelValueDto>();
}
