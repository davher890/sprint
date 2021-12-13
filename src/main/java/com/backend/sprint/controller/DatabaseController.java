package com.backend.sprint.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.service.database.DatabaseExternalAthletesService;
import com.backend.sprint.service.database.DatabaseHistoricService;
import com.backend.sprint.service.database.DatabaseSportSchoolAthletesService;

@RestController
@RequestMapping("database")
public class DatabaseController {

	private static String dbDemoPath = "BD/Demo/";

	private static String dbPath = "BD/";

	@Autowired
	private DatabaseSportSchoolAthletesService service;

	@Autowired
	private DatabaseExternalAthletesService externalService;

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

	@GetMapping("demo/{id}/athletes")
	public List<AthleteDto> dbFillAtheltesDemoData(@PathVariable(name = "id") int sportSchoolId)
			throws IOException, ParseException {
		String csvName = dbDemoPath + "BD" + sportSchoolId + ".csv";
		return service.dbFillAtheltesData(sportSchoolId, csvName);
	}

	@GetMapping("/externo/athletes")
	public List<AthleteDto> dbFillExternalAtheltesData() throws IOException, ParseException {
		String csvName = dbPath + "BD_EXTERNOS.csv";
		return externalService.dbFillAtheltesData(csvName);
	}

	@GetMapping("historic")
	public List<AthleteDto> dbFillAtheltesHistoric() throws IOException, ParseException {
		String csvName = "BD_HISTORICO.csv";
		return historicService.dbFillAtheltesData(csvName);
	}

}
