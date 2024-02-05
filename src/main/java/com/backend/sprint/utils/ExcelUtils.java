package com.backend.sprint.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

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

	public static BufferedReader convertExcelToCsv(MultipartFile excelFile, String[] mandatoryColumns,
			char cellSeparator) throws Exception {
		if (excelFile.isEmpty()) {
			throw new Exception("File is empty");
		}

		InputStream inputStream = excelFile.getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0); // Assuming you are working with the first sheet

		Row headerRow = sheet.getRow(0);
		List<String> headers = new ArrayList<String>();

		if (headerRow != null) {
			Iterator<Cell> cellIterator = headerRow.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				headers.add(cell.toString().toUpperCase());
			}
		}

		// Check Headers
		if (mandatoryColumns != null && mandatoryColumns.length > 0) {
			for (String header : mandatoryColumns) {
				if (!headers.contains(header)) {
					throw new Exception("Columna " + header + " no se encontro en el archivo");
				}
			}
		}

		// Iterate through rows and columns to write CSV content
		Iterator<Row> rowIterator = sheet.iterator();
//		rowIterator.next(); // Move to the next row (skip headers)

		// Prepare a StringWriter to write CSV content
		StringWriter csvWriter = new StringWriter();

		while (rowIterator.hasNext()) {
			int columnLength = 0;
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				// Append cell data to CSV content
				csvWriter.append(cell.toString());

				if (cellIterator.hasNext()) {
					csvWriter.append(cellSeparator);
					columnLength++;
				}
			}

//			csvWriter.append(cellSeparator);
			if (rowIterator.hasNext()) {
				csvWriter.append('\n');
			}
		}

		// Save the CSV content to a file
		String csvFileName = "output.csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName))) {
			writer.write(csvWriter.toString());
		}

		workbook.close();

		return new BufferedReader(new InputStreamReader(excelFile.getInputStream(), "UTF-8"));
	}

}
