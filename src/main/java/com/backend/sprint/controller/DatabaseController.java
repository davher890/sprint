package com.backend.sprint.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.service.DatabaseService;

@RestController
@RequestMapping("database")
public class DatabaseController {

	@Autowired
	private DatabaseService service;

	@GetMapping("sport_schools")
	public Set<SportSchoolDto> dbFillSportSchoolsData() throws IOException, ParseException {
		return service.dbFillSportSchoolsData();
	}

	@GetMapping("trainers")
	public Set<TrainerDto> dbFillTrainersData() throws IOException, ParseException {
		return service.dbFillTrainersData();
	}

	@GetMapping("schedules")
	public Set<ScheduleDto> dbFillSchedulesData() throws IOException, ParseException {
		return service.dbFillSchedulesData();
	}

	@GetMapping("groups")
	public Set<GroupDto> dbFillGroupsData() throws IOException, ParseException {
		return service.dbFillGroupsData();
	}

	@GetMapping("athletes")
	public Set<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {
		return service.dbFillAtheltesData();
	}

}
