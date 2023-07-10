package com.backend.sprint.service.database;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.service.AthleteService;

@Service
public class DatabaseUpdateRelativesService extends DatabaseService {

	List<String> seniorDorsalCaterogies = Arrays
			.asList(new String[] { "mm", "mf", "sm", "sf", "u20f", "u20m", "u23f", "u23m" });

	@Autowired
	private AthleteService athleteService;

	private Logger logger = LoggerFactory.getLogger(DatabaseUpdateRelativesService.class);

	@Transactional
	private void saveAthlete(AthleteDto fileAthlete, AthleteDto athlete) {

		athlete.setFamiliarOneName(fileAthlete.getFamiliarOneName());
		athlete.setFamiliarOneFirstSurname(fileAthlete.getFamiliarOneFirstSurname());
		athlete.setFamiliarOneSecondSurname(fileAthlete.getFamiliarOneSecondSurname());
		athlete.setFamiliarTwoName(fileAthlete.getFamiliarTwoName());
		athlete.setFamiliarTwoFirstSurname(fileAthlete.getFamiliarTwoFirstSurname());
		athlete.setFamiliarTwoSecondSurname(fileAthlete.getFamiliarTwoSecondSurname());

		athleteService.save(athlete);
	}

	@Transactional
	public void updateAthleteRelatives(List<AthleteDto> fileAthletes) {

		fileAthletes.parallelStream().forEach(fileAthlete -> {
			AthleteDto athlete = athleteService.findByCode(fileAthlete.getCode());

			if (athlete != null) {
				saveAthlete(fileAthlete, athlete);
			} else {
				logger.info(fileAthlete.getCode() + "; El atleta no se encontro en la plataforma");
			}
		});
	}

}
