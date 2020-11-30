package com.backend.sprint.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;

public abstract class ExcelUtils {

	public static Sheet generateExcel(XSSFWorkbook workbook, String sheetName, List<ExcelDataDto> data) {
		// Create a Sheet
		Sheet sheet = workbook.createSheet(sheetName);

		// Creating data rows for each customer
		IntStream.range(0, data.size()).forEach(i -> {

			Row dataRow = sheet.createRow(i);
			IntStream.range(0, data.get(i).getData().size()).forEach(j -> {
				List<ExcelValueDto> data2 = data.get(i).getData();
				Cell cell = dataRow.createCell(j, data2.get(j).getType());
				cell.setCellValue(data2.get(j).getValue());
				if (data2.get(j).getStyle() != null) {
					cell.setCellStyle(data2.get(j).getStyle());
				}
			});
		});
		return sheet;

	}

	public static ByteArrayInputStream generateExcel(String sheetName, List<ExcelDataDto> data) throws IOException {

		Workbook workbook = new XSSFWorkbook();

		// Create a Sheet
		Sheet sheet = workbook.createSheet(sheetName);

		// Creating data rows for each customer
		IntStream.range(0, data.size()).forEach(i -> {

			Row dataRow = sheet.createRow(i);
			IntStream.range(0, data.get(i).getData().size()).forEach(j -> {
				List<ExcelValueDto> data2 = data.get(i).getData();
				Cell cell = dataRow.createCell(j, data2.get(j).getType());
				cell.setCellValue(data2.get(j).getValue());
				if (data2.get(j).getStyle() != null) {
					cell.setCellStyle(data2.get(j).getStyle());
				}
			});
		});

		IntStream.range(0, data.size()).parallel().forEach(i -> {
			sheet.autoSizeColumn(i);
		});

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	public static CellType getCellType(ColumnDto d) {
		switch (d.getType()) {
		case "number":
			return CellType.NUMERIC;
		case "bool":
			return CellType.BOOLEAN;
		default:
			return CellType.STRING;
		}
	}
}
