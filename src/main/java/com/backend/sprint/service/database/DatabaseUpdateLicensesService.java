package com.backend.sprint.service.database;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.JobResultLog;
import com.backend.sprint.service.AthleteService;

@Service
public class DatabaseUpdateLicensesService extends DatabaseService {

	List<String> seniorDorsalCaterogies = Arrays
			.asList(new String[] { "mm", "mf", "sm", "sf", "u20f", "u20m", "u23f", "u23m" });

	@Autowired
	private AthleteService athleteService;

	private Logger logger = LoggerFactory.getLogger(DatabaseUpdateLicensesService.class);

	@Transactional
	private void saveAthlete(AthleteDto fileAthlete, AthleteDto athlete) {
		athlete.setDorsalNumber(fileAthlete.getDorsalNumber());
		// License
		athlete.setLicenseType(fileAthlete.getLicenseType());
		athlete.setLicense(fileAthlete.getLicense());
		// Dorsal
		athlete.setDorsalNumber(fileAthlete.getDorsalNumber());
		athlete.setDorsalCategory(fileAthlete.getDorsalCategory().toLowerCase());
		// Fee
		switch (fileAthlete.getLicenseType().trim().toUpperCase()) {
		case "T":
			athlete.setLicenseTerritorialFee(fileAthlete.getLicenseTerritorialFee());
			athlete.setLicenseType("TERRITORIAL");
			break;
		case "N":
			athlete.setLicenseNationalFee(fileAthlete.getLicenseTerritorialFee());
			athlete.setLicenseType("NATIONAL");
			break;
		default:
			break;
		}
		if (athlete.getLicenseNationalFee() > 0) {
			athlete.setLicenseType("NATIONAL");
		} else {
			athlete.setLicenseType("TERRITORIAL");
		}
		logger.info(fileAthlete.getDni() + "; " + fileAthlete.getName() + "; " + fileAthlete.getFirstSurname() + ";"
				+ fileAthlete.getSecondSurname() + ";" + athlete.getLicenseType() + ";" + athlete.getLicense() + ";"
				+ athlete.getDorsalCategory() + ";" + athlete.getDorsalNumber());

		athleteService.save(athlete);
	}

	public void updateAthleteLicenses(List<AthleteDto> fileAthletes) {

		fileAthletes.parallelStream().forEach(fileAthlete -> {
			List<AthleteDto> athletes = null;
			if (seniorDorsalCaterogies.contains(fileAthlete.getDorsalCategory().toLowerCase())) {
				athletes = athleteService.findByDni(fileAthlete.getDni());
			} else {
				athletes = athleteService.findByName(fileAthlete.getName(), fileAthlete.getFirstSurname(),
						fileAthlete.getSecondSurname());
			}
			if (athletes != null && !athletes.isEmpty()) {
				for (AthleteDto a : athletes) {
					saveAthlete(fileAthlete, a);
				}
			} else {
				logger.info(fileAthlete.getDni() + "; El atleta no se encontro en la plataforma");
			}
		});
	}

	public JobResultLog updateAthleteLicenseFee(List<AthleteDto> fileAthletes) {

		JobResultLog log = new JobResultLog();

		fileAthletes.parallelStream().forEach(fileAthlete -> {
			List<AthleteDto> athletes = athleteService.findByLicense(fileAthlete.getLicense());
			if (athletes != null && !athletes.isEmpty()) {
				for (AthleteDto athlete : athletes) {
					switch (fileAthlete.getLicenseType().trim().toUpperCase()) {
					case "T":
						athlete.setLicenseTerritorialFee(fileAthlete.getLicenseTerritorialFee());
						athlete.setLicenseType("TERRITORIAL");
						break;
					case "N":
						athlete.setLicenseNationalFee(fileAthlete.getLicenseTerritorialFee());
						athlete.setLicenseType("NATIONAL");
						break;
					default:
						break;
					}
					if (athlete.getLicenseNationalFee() > 0) {
						athlete.setLicenseType("NATIONAL");
					} else {
						athlete.setLicenseType("TERRITORIAL");
					}
					logger.info(fileAthlete.getDni() + "; " + fileAthlete.getName() + "; "
							+ fileAthlete.getFirstSurname() + ";" + fileAthlete.getSecondSurname() + ";"
							+ athlete.getLicenseType() + ";" + athlete.getLicense() + ";" + athlete.getDorsalCategory()
							+ ";" + athlete.getDorsalNumber());

					athlete = athleteService.save(athlete);
					log.getInfoMessages()
							.add("Cuotas actualizadas para el atleta con licencia " + athlete.getLicense()
									+ (fileAthlete.getLicenseType().trim().toUpperCase().equals("N")
											? ".\n Cuota nacional: " + athlete.getLicenseNationalFee()
											: ".\n Cuota territorial: " + athlete.getLicenseTerritorialFee()));
//					} else {
//						log.getInfoMessages().add("No se pudo actualizar las cuotas para el atleta con licencia "
//								+ athlete.getLicense() + "\n Licencia: " + athlete.getLicense() + "-"
//								+ fileAthlete.getLicense() + "\n Cuota nacional: " + athlete.getLicenseNationalFee()
//								+ "-" + fileAthlete.getLicenseNationalFee() + ".\n Cuota territorial: "
//								+ athlete.getLicenseTerritorialFee() + "-" + fileAthlete.getLicenseTerritorialFee());
//					}
				}
			} else {
				log.getErrorMessages()
						.add("El atleta con licencia " + fileAthlete.getLicense() + " no se encontro en la plataforma");
				logger.info(fileAthlete.getDni() + "; El atleta no se encontro en la plataforma");
			}
		});
		return log;
	}

}
