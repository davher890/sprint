package com.backend.sprint.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.service.database.DatabaseExternalAthletesService;
import com.backend.sprint.service.database.DatabaseHistoricService;
import com.backend.sprint.service.database.DatabaseParentsAthletesService;
import com.backend.sprint.service.database.DatabaseSportSchoolAthletesService;
import com.backend.sprint.service.database.DatabaseUpdateLicensesService;
import com.backend.sprint.service.database.DatabaseUpdateRelativesService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RestController
@RequestMapping("database")
public class DatabaseController {

	@Autowired
	private DatabaseSportSchoolAthletesService service;

	@Autowired
	private DatabaseUpdateLicensesService updateLicensesService;

	@Autowired
	private DatabaseUpdateRelativesService updateRelativesService;

	@Autowired
	private DatabaseExternalAthletesService externalService;

	@Autowired
	private DatabaseParentsAthletesService parentsService;

	@Autowired
	private DatabaseHistoricService historicService;

	@GetMapping("/{id}/athletes")
	public List<AthleteDto> dbFillAtheltesData(@PathVariable(name = "id") int sportSchoolId)
			throws IOException, ParseException {
		String csvName = null;
		if (sportSchoolId == 1) {
			csvName = "BD_SANTA_ANA.csv";
		}
		if (sportSchoolId == 2) {
			csvName = "BD_PARACUELLOS.csv";
		}
		if (csvName == null) {
			return null;
		}
		return service.dbFillAtheltesData(sportSchoolId, csvName);
	}

//	@GetMapping("demo/{id}/athletes")
//	public List<AthleteDto> dbFillAtheltesDemoData(@PathVariable(name = "id") int sportSchoolId)
//			throws IOException, ParseException {
//		String csvName = dbDemoPath + "BD" + sportSchoolId + ".csv";
//		return service.dbFillAtheltesData(sportSchoolId, csvName);
//	}

	@GetMapping("/externo/athletes")
	public List<AthleteDto> dbFillExternalAtheltesData() throws IOException, ParseException {
		String csvName = "BD_EXTERNOS.csv";
		return externalService.dbFillAtheltesData(csvName);
	}

	@GetMapping("/padres/athletes")
	public List<AthleteDto> dbFillPadresAtheltesData() throws IOException, ParseException {
		String csvName = "BD_PADRES.csv";
		return parentsService.dbFillAtheltesData(csvName);
	}

	@GetMapping("historic")
	public List<AthleteDto> dbFillAtheltesHistoric() throws IOException, ParseException {
		String csvName = "BD_HISTORICO.csv";
		return historicService.dbFillAtheltesData(csvName);
	}

	@GetMapping("/{id}/check")
	public List<AthleteDto> checkSportSchoolDb(@PathVariable(name = "id") int sportSchoolId,
			@RequestParam("file") MultipartFile file) throws IOException, ParseException {

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));

		CsvToBean<AthleteDto> csvToAthlete = new CsvToBeanBuilder<AthleteDto>(fileReader).withSeparator(';')
				.withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(true).withType(AthleteDto.class).build();
		List<AthleteDto> athletes = (List<AthleteDto>) csvToAthlete.parse();

		service.checkSportSchoolIdAthletes(sportSchoolId, athletes);

		return athletes;
	}

	@GetMapping("/update_licenses")
	public List<AthleteDto> updateAthleteLicenses(@RequestParam("file") MultipartFile file)
			throws IOException, ParseException {

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));

		CsvToBean<AthleteDto> csvToAthlete = new CsvToBeanBuilder<AthleteDto>(fileReader).withProfile("update_licenses")
				.withSeparator(';').withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(true)
				.withType(AthleteDto.class).build();
		List<AthleteDto> athletes = (List<AthleteDto>) csvToAthlete.parse();

		updateLicensesService.updateAthleteLicenses(athletes
		// .parallelStream()
		// .filter(d ->
		// d.getDni().toUpperCase().equals("REDACTED")).collect(Collectors.toList())
		);

		return athletes;
	}

	@GetMapping("/update_relatives")
	public List<AthleteDto> updateAthleteRelatives(@RequestParam("file") MultipartFile file)
			throws IOException, ParseException {

		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));

		CsvToBean<AthleteDto> csvToAthlete = new CsvToBeanBuilder<AthleteDto>(fileReader)
				.withProfile("update_relatives").withSeparator(';').withIgnoreLeadingWhiteSpace(true)
				.withIgnoreEmptyLine(true).withType(AthleteDto.class).build();
		List<AthleteDto> athletes = (List<AthleteDto>) csvToAthlete.parse();

		updateRelativesService.updateAthleteRelatives(athletes
		// .parallelStream()
		// .filter(d ->
		// d.getDni().toUpperCase().equals("REDACTED")).collect(Collectors.toList())
		);

		return athletes;
	}

}
