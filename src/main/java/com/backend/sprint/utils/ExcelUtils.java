package com.backend.sprint.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backend.sprint.model.dto.ExcelDataDto;

public abstract class ExcelUtils {

	public static ByteArrayInputStream generateExcel(String sheetName, List<String> headers, List<ExcelDataDto> data)
			throws IOException {

		Workbook workbook = new XSSFWorkbook();

		// Create a Sheet
		Sheet sheet = workbook.createSheet(sheetName);

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Create header cells
		IntStream.range(0, headers.size()).forEach(i -> {
			Cell headerCell = headerRow.createCell(i);
			headerCell.setCellValue(headers.get(i));
			headerCell.setCellStyle(headerCellStyle);
		});

		// Creating data rows for each customer
		IntStream.range(0, data.size()).forEach(i -> {

			Row dataRow = sheet.createRow(i + 1);
			IntStream.range(0, data.get(i).getData().size()).forEach(j -> {
				dataRow.createCell(j, data.get(i).getData().get(j).getType())
						.setCellValue(data.get(i).getData().get(j).getValue());
			});
		});

		IntStream.range(0, headers.size()).forEach(i -> {
			sheet.autoSizeColumn(i);
		});

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return new ByteArrayInputStream(outputStream.toByteArray());

	}
}
