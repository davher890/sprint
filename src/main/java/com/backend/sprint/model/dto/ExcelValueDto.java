package com.backend.sprint.model.dto;

import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelValueDto {

	private String value;
	private Date dateValue;

	private CellType type;

	private CellStyle style;

	ColumnDto columnDto;

	public ExcelValueDto(String value) {
		this.value = value;
		this.type = CellType.STRING;
	}

	public ExcelValueDto(String value, ColumnDto columnDto) {
		this.value = value;
		this.columnDto = new ColumnDto();
		this.columnDto.setDataField(columnDto.getDataField());
		this.columnDto.setType(columnDto.getType());
		this.columnDto.setShow(columnDto.isShow());
		this.columnDto.setText(columnDto.getText());

		this.type = this.getCellType(columnDto);
	}

	public ExcelValueDto(Date value, ColumnDto columnDto) {
		this.dateValue = value;
		this.columnDto = new ColumnDto();
		this.columnDto.setDataField(columnDto.getDataField());
		this.columnDto.setType("date");
		this.columnDto.setShow(columnDto.isShow());
		this.columnDto.setText(columnDto.getText());
		this.type = this.getCellType(columnDto);
	}

	public ExcelValueDto(String value, CellType type) {
		this.value = value;
		this.type = type;
	}

	public ExcelValueDto(String value, CellType type, CellStyle style) {
		this.value = value;
		this.type = type;
		this.style = style;
	}

	private CellType getCellType(ColumnDto d) {
		if (d.getType() == null) {
			return CellType.STRING;
		}
		switch (d.getType()) {
		case "number":
			return CellType.NUMERIC;
		case "bool":
			return CellType.BOOLEAN;
		default:
			return CellType.STRING;
		}
	}

	public CellStyle getStyle(Workbook wb) {

		if (this.columnDto.getType() != null && this.columnDto.getType().equals("date")) {
			CellStyle cellStyle = wb.createCellStyle();
			CreationHelper createHelper = wb.getCreationHelper();
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));
			return cellStyle;
		}
		return style;
	}

}
