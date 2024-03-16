package com.backend.sprint.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.sprint.model.dto.AthleteLicenseDto;
import com.backend.sprint.model.dto.JobResultLog;
import com.backend.sprint.service.database.DatabaseUpdateLicensesService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RestController
@RequestMapping("licenses")
public class LicenseController {

	@Autowired
	private DatabaseUpdateLicensesService updateLicensesService;

	private final static String[] MANDATORY_COLUMNS = new String[] { "NUM_LICENCIA", "CUOTA_LICENCIA",
			"TIPO_LICENCIA" };
	private final static char CELL_SEPARATOR = ',';

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public JobResultLog uploadLicenses(@RequestParam("file") MultipartFile file) throws Exception {

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
		String headers = fileReader.readLine();
		JobResultLog log = new JobResultLog();
		for (String column : MANDATORY_COLUMNS) {
			if (!headers.contains(column)) {
				log.getErrorMessages().add("No se encontro la columna " + column);
			}
		}
		if (log.getErrorMessages().size() > 0) {
			return log;

		} else {
			fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
			CsvToBean<AthleteLicenseDto> csvToAthlete = new CsvToBeanBuilder<AthleteLicenseDto>(fileReader)
					.withProfile("update_licenses").withSeparator(CELL_SEPARATOR).withIgnoreLeadingWhiteSpace(true)
					.withIgnoreEmptyLine(true).withType(AthleteLicenseDto.class).build();
			List<AthleteLicenseDto> athletes = (List<AthleteLicenseDto>) csvToAthlete.parse();

			updateLicensesService.removeLicenses();
			return updateLicensesService.updateAthleteLicenseFee(athletes);
		}

	}

}
