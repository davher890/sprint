package com.backend.sprint.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.service.AthleteService;

@Service
public class DeleteUnregisteredAthletesGroupCron {

	@Autowired
	private AthleteService athleteService;

	private Logger logger = LoggerFactory.getLogger(DeleteUnregisteredAthletesGroupCron.class);

	// Every day 5 AM
//	@Scheduled(cron = "0 0 5 * * *")
//	private void deleteUnregisteredAthletesGroup() {
//		logger.info("Triggering scheduled cron");
//		athleteService.deleteGroupUnregisteredAthletes();
//		logger.info("Scheduled cron finished");
//	}

}
