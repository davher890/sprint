package com.backend.sprint.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.service.database.DatabaseAthleteService;
import com.backend.sprint.service.database.DatabaseGroupService;
import com.backend.sprint.service.database.DatabaseHistoricService;

@RestController
@RequestMapping("database")
public class DatabaseController {

	@Autowired
	private DatabaseAthleteService service;

	@Autowired
	private DatabaseGroupService dbGroupService;

	@Autowired
	private DatabaseHistoricService historicService;

	@GetMapping("sport_schools")
	public Set<SportSchoolDto> dbFillSportSchoolsData() throws IOException, ParseException {
		return service.dbFillSportSchoolsData();
	}

	@GetMapping("trainers")
	public Set<TrainerDto> dbFillTrainersData() throws IOException, ParseException {
		return service.dbFillTrainersData();
	}

	@GetMapping("groups")
	public List<GroupDto> dbFillGroupsData() throws IOException, ParseException {
		String santanaFileName = "/Users/david/Development/git/sprint/src/main/resources/GRUPOS_SANTA_ANA.csv";
		String paracuellosFileName = "/Users/david/Development/git/sprint/src/main/resources/GRUPOS_PARACUELLOS.csv";

		List<GroupDto> sGroups = dbGroupService.dbFillGroupsData(santanaFileName);
		List<GroupDto> pGroups = dbGroupService.dbFillGroupsData(paracuellosFileName);

		sGroups.addAll(pGroups);
		return sGroups;
	}

	@GetMapping("athletes")
	public List<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {
		return service.dbFillAtheltesData();
	}

	@GetMapping("historic")
	public List<AthleteDto> dbFillAtheltesHistoric() throws IOException, ParseException {
		return historicService.dbFillAtheltesData();
	}

}
