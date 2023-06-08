package com.backend.sprint.cron;

import org.springframework.beans.factory.annotation.Autowired;

import com.backend.sprint.service.AthleteService;

public class AthleteRegisterDateCron {

	@Autowired
	private AthleteService athleteService;

	// Every day 5 AM
//	@Scheduled(cron = "0 0 5 * * *")
//	private void cronUpdateAthletesRegisterDate() {
//		athleteService.updateAthletesRegisterDate();
//	}

}
