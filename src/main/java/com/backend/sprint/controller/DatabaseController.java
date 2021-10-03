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

	@Autowired
	private DatabaseSportSchoolAthletesService service;

	@Autowired
	private DatabaseExternalAthletesService externalService;

//	@Autowired
//	private DatabaseGroupService dbGroupService;

	@Autowired
	private DatabaseHistoricService historicService;

//	@GetMapping("sport_schools")
//	public Set<SportSchoolDto> dbFillSportSchoolsData() throws IOException, ParseException {
//		return service.dbFillSportSchoolsData();
//	}

//	@GetMapping("trainers")
//	public Set<TrainerDto> dbFillTrainersData() throws IOException, ParseException {
//		return service.dbFillTrainersData();
//	}

//	@GetMapping("groups")
//	public List<GroupDto> dbFillGroupsData() throws IOException, ParseException {
//		String santanaFileName = System.getProperty("user.dir") + "/src/main/resources/GRUPOS_SANTA_ANA.csv";
//		String paracuellosFileName = System.getProperty("user.dir") + "/src/main/resources/GRUPOS_PARACUELLOS.csv";
//
//		List<GroupDto> sGroups = dbGroupService.dbFillGroupsData(santanaFileName);
//		List<GroupDto> pGroups = dbGroupService.dbFillGroupsData(paracuellosFileName);
//
//		sGroups.addAll(pGroups);
//		return sGroups;
//	}

	@GetMapping("/{id}/athletes")
	public List<AthleteDto> dbFillAtheltesData(@PathVariable(name = "id") int sportSchoolId)
			throws IOException, ParseException {
		String csvName = "BD_SANTA_ANA.csv";
		if (sportSchoolId == 2) {
			csvName = "BD_PARACUELLOS.csv";
		}
		return service.dbFillAtheltesData(sportSchoolId, csvName);
	}

	@GetMapping("/externo/athletes")
	public List<AthleteDto> dbFillExternalAtheltesData() throws IOException, ParseException {
		String csvName = "BD_EXTERNOS.csv";
		return externalService.dbFillAtheltesData(csvName);
	}

	@GetMapping("historic")
	public List<AthleteDto> dbFillAtheltesHistoric() throws IOException, ParseException {
		return historicService.dbFillAtheltesData();
	}

}
