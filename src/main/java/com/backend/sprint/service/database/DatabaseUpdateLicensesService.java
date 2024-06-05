package com.backend.sprint.service.database;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteLicenseDto;
import com.backend.sprint.model.dto.JobResultLog;
import com.backend.sprint.repository.AthleteRepository;
import com.backend.sprint.service.AthleteService;

@Service
public class DatabaseUpdateLicensesService extends DatabaseService {

	List<String> seniorDorsalCaterogies = Arrays
			.asList(new String[] { "mm", "mf", "sm", "sf", "u20f", "u20m", "u23f", "u23m" });

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private AthleteRepository athleteRepository;

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

	@Transactional
	public void removeLicenses() {
		athleteRepository.removeLicenses();
	}

	public JobResultLog updateAthleteLicenseFee(List<AthleteLicenseDto> fileAthletes) {

		JobResultLog log = new JobResultLog();

		fileAthletes.parallelStream().forEach(fileAthlete -> {
			List<AthleteDao> athletes = athleteRepository.findByLicense(fileAthlete.getNumLicencia());
			if (athletes != null && !athletes.isEmpty()) {
				for (AthleteDao athlete : athletes) {
					String tipoLicencia = fileAthlete.getTipoLicencia().trim().toUpperCase();
					switch (tipoLicencia) {
					case "T":
						athlete.setLicenseTerritorialFee(
								fileAthlete.getCuotaLicencia() + fileAthlete.getCAdaptacionTerritorial()
										+ fileAthlete.getCEmisionCarnet() + fileAthlete.getCIndeAClub()
										+ fileAthlete.getCClubAInde() + fileAthlete.getCFueraPlazoInde()
										+ fileAthlete.getCFueraPlazo() + fileAthlete.getCClubOtraAutonomia()
										+ fileAthlete.getCIndependiente() + fileAthlete.getCPasoTAN()
										+ fileAthlete.getCAdaptacionMundial() + fileAthlete.getCAdaptacionEuropa()
										+ fileAthlete.getCSeguroTraiLn() + fileAthlete.getCSeguroDep());
						athlete.setLicenseType("TERRITORIAL");
						break;
					case "N":
						athlete.setLicenseNationalFee(
								fileAthlete.getCuotaLicencia() + fileAthlete.getCAdaptacionTerritorial()
										+ fileAthlete.getCEmisionCarnet() + fileAthlete.getCIndeAClub()
										+ fileAthlete.getCClubAInde() + fileAthlete.getCFueraPlazoInde()
										+ fileAthlete.getCFueraPlazo() + fileAthlete.getCClubOtraAutonomia()
										+ fileAthlete.getCIndependiente() + fileAthlete.getCPasoTAN()
										+ fileAthlete.getCAdaptacionMundial() + fileAthlete.getCAdaptacionEuropa()
										+ fileAthlete.getCSeguroTraiLn() + fileAthlete.getCSeguroDep());
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
					athlete.setLicenseOtherFee(fileAthlete.getCAdaptacionTerritorial() + fileAthlete.getCEmisionCarnet()
							+ fileAthlete.getCIndeAClub() + fileAthlete.getCClubAInde()
							+ fileAthlete.getCFueraPlazoInde() + fileAthlete.getCFueraPlazo()
							+ fileAthlete.getCClubOtraAutonomia() + fileAthlete.getCIndependiente()
							+ fileAthlete.getCPasoTAN() + fileAthlete.getCAdaptacionMundial()
							+ fileAthlete.getCAdaptacionEuropa() + fileAthlete.getCSeguroTraiLn()
							+ fileAthlete.getCSeguroDep());
					athleteRepository.save(athlete);
				}
			} else {
				log.getErrorMessages().add(
						"El atleta con licencia " + fileAthlete.getNumLicencia() + " no se encontro en la plataforma");
				logger.info(fileAthlete.getNumId() + "; El atleta no se encontro en la plataforma");
			}
		});
		if (log.getErrorMessages() == null || log.getErrorMessages().isEmpty()) {
			log.getInfoMessages().add("Se actualizaron las licencias de todos los atletas.");
		}
		return log;
	}

}
