package com.backend.sprint.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;

public abstract class ExcelUtils {

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
	private final static DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	public static Sheet generateExcel(XSSFWorkbook workbook, String sheetName, List<ExcelDataDto> data) {
		// Create a Sheet
		Sheet sheet = workbook.createSheet(sheetName.replaceAll("[\\\\:?\\/*\\[\\]]", "_"));

		// Creating data rows for each customer
		writeRows(data, workbook, sheet);
		return sheet;

	}

	public static ByteArrayOutputStream generateExcel(String sheetName, List<ExcelDataDto> data) throws IOException {

		Workbook workbook = new XSSFWorkbook();

		// Create a Sheet
		Sheet sheet = workbook.createSheet(sheetName);

		// Creating data rows for each customer
		writeRows(data, workbook, sheet);

		IntStream.range(0, data.size()).parallel().forEach(i -> {
			sheet.autoSizeColumn(i);
		});

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return outputStream;
	}

	private static void writeRows(List<ExcelDataDto> data, Workbook wb, Sheet sheet) {
		IntStream.range(0, data.size()).forEach(i -> {

			Row dataRow = sheet.createRow(i);
			ExcelDataDto excelDataDto = data.get(i);
			IntStream.range(0, excelDataDto.getData().size()).forEach(j -> {
				List<ExcelValueDto> data2 = excelDataDto.getData();
				ExcelValueDto excelValueDto = data2.get(j);
				Cell cell = dataRow.createCell(j, excelValueDto.getType());
				if (excelValueDto.getColumnDto() != null && excelValueDto.getColumnDto().getType() != null
						&& excelValueDto.getColumnDto().getType().equals("date")) {
					if (excelValueDto.getLocalDateValue() != null) {
						cell.setCellValue(excelValueDto.getLocalDateValue().format(localDateFormatter));
					} else if (excelValueDto.getDateValue() != null) {
						cell.setCellValue(dateFormatter.format(excelValueDto.getDateValue()));
					}
					cell.setCellStyle(excelValueDto.getStyle(wb));
				} else {
					cell.setCellValue(excelValueDto.getValue());
				}
			});
		});
	}
}
