package com.backend.sprint.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.service.database.DatabaseUpdateLicensesService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RestController
@RequestMapping("licenses")
public class LicenseController {

	@Autowired
	private DatabaseUpdateLicensesService updateLicensesService;

	private static String[] MANDATORY_HEADERS = new String[] { "NUM_LICENCIA", "CUOTA_LICENCIA", "TIPO_LICENCIA" };
//	private static String NUM_LICENCIA_HEADER = "NUM_LICENCIA";

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uploadLicenses(@RequestParam("file") MultipartFile file) throws Exception {

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}

		try {
			BufferedReader fileReader = convertExcelToCsv(file);
			CsvToBean<AthleteDto> csvToAthlete = new CsvToBeanBuilder<AthleteDto>(fileReader)
					.withProfile("update_licenses").withSeparator(';').withIgnoreLeadingWhiteSpace(true)
					.withIgnoreEmptyLine(true).withType(AthleteDto.class).build();
			List<AthleteDto> athletes = (List<AthleteDto>) csvToAthlete.parse();

			updateLicensesService.updateAthleteLicenses(athletes);

			return ResponseEntity.ok("Licencias actualizadas segun el archivo: " + file.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error procesando el archivo excel the Excel." + e.getMessage());
		}
	}

	private BufferedReader convertExcelToCsv(MultipartFile excelFile) throws Exception {
		if (excelFile.isEmpty()) {
			throw new Exception("File is empty");
		}

		InputStream inputStream = excelFile.getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0); // Assuming you are working with the first sheet

		// Prepare a StringWriter to write CSV content
		StringWriter csvWriter = new StringWriter();

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
		for (String header : MANDATORY_HEADERS) {
			if (!headers.contains(header)) {
				throw new Exception("Columna " + header + " no se encontro en el archivo");
			}
		}

		// Iterate through rows and columns to write CSV content
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next(); // Move to the next row (skip headers)

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				// Append cell data to CSV content
				csvWriter.append(cell.toString());

				if (cellIterator.hasNext()) {
					csvWriter.append(',');
				}
			}

			csvWriter.append('\n');
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
