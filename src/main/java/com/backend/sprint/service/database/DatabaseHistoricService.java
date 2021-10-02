package com.backend.sprint.service.database;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.NameDataDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.FamilyService;
import com.backend.sprint.service.HistoricService;
import com.backend.sprint.utils.HistoricType;
import com.opencsv.CSVReader;

@Service
public class DatabaseHistoricService extends DatabaseService {

	private static final String EXT = "EXT";
	private static final String PAR = "PAR";
	private static final String STA = "STA";

	private static final int FAMILY_CODE = 0;
	private static final int ATHLETE_CODE = 1;
	private static final int SPORT_SCHOOL = 2;
	private static final int ATHLETE = 3;
	private static final int ATHLETE_SURNAMES = 4;
	private static final int ATHLETE_NAME = 5;
	private static final int BIRTHDATE = 6;
	private static final int YEAR = 7;
	private static final int AGE = 8;
	private static final int GENDER = 9;
	private static final int DNI = 10;
	private static final int PHONE_1 = 11;
	private static final int PHONE_2 = 12;
	private static final int PHONE_3 = 13;
	private static final int ADDREES = 14;
	private static final int POSTAL_CODE = 15;
	private static final int MUNICIPALITY = 16;
	private static final int FATHER_MAIL = 17;
	private static final int MOTHER_MAIL = 18;
	private static final int MAIL = 19;
	private static final int FATHER_NAME = 20;
	private static final int MOTHER_NAME = 21;
	private static final int HOLDER_NAME = 22;
	private static final int HOLDER_DNI = 23;
	private static final int IBAN = 24;
	private static final int OBSERVATIONS = 25;
	private static final int REGISTER_DATE = 26;
	private static final int UNREGISTER_DATE = 27;

	private static final String DATE_SEPARATOR = "-";

	private Logger logger = LoggerFactory.getLogger(DatabaseHistoricService.class);

	@Autowired
	private FamilyService familyService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private HistoricService historicService;

	public List<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {

		Long[] athleteTestList = { 2381L, 3581L, 3681L, 3702L, 11861L, 26652L, 1913L };

		CSVReader csvReader = readFile(System.getProperty("user.dir") + "/src/main/resources/BD_HISTORICO.csv");
		return StreamSupport.stream(csvReader.spliterator(), true)// .parallel()
//				.filter(d -> Arrays.asList(athleteTestList).contains(Long.valueOf(d[ATHLETE_CODE].trim())))
				.map(line -> {
					try {

						long familyCode = Long.valueOf(line[FAMILY_CODE].trim());

						long athleteCode = Long.valueOf(line[ATHLETE_CODE].trim());
						List<String> split = Arrays.asList(line[ATHLETE_SURNAMES].split(" "));
						String firstSurname = split.get(0).trim();
						String secondSurname = "";
						if (split.size() > 1) {
							secondSurname = split.subList(1, split.size()).stream().reduce((identity, accumulator) -> {
								return identity.trim() + " " + accumulator.trim();
							}).orElse(null);
						}
						String name = line[ATHLETE_NAME].trim();

						Date birthDate = null;
						if (!line[BIRTHDATE].isEmpty() && !line[BIRTHDATE].trim().isEmpty()) {
							birthDate = dateFormat.parse(line[BIRTHDATE].trim());
						}
						String gender = line[GENDER].trim().equals("Hombre") ? "male" : "female";
						String dni = line[DNI].trim();

						String phone1 = line[PHONE_1].trim();
						String phone2 = line[PHONE_2].trim();
						String phone3 = line[PHONE_3].trim();

						String address = line[ADDREES].trim();
						String postalCode = line[POSTAL_CODE].trim();
						String municipality = line[MUNICIPALITY].trim();
						String mail = line[MAIL].trim();

						NameDataDto father = getNameData(line, FATHER_NAME);
						String fatherName = father.getName();
						String fatherFirstSurname = father.getFirstSurname();
						String fatherSecondSurname = father.getSecondSurname();

						NameDataDto mother = getNameData(line, MOTHER_NAME);
						String motherName = mother.getName();
						String motherFirstSurname = mother.getFirstSurname();
						String motherSecondSurname = mother.getSecondSurname();

						String iban = line[IBAN].trim();
						if (iban.equals("00000000000000000000")) {
							iban = null;
						}

						NameDataDto holder = getNameData(line, HOLDER_NAME);
						String holderName = holder.getName();
						String holderFirstSurname = holder.getFirstSurname();
						String holderSecondSurname = holder.getSecondSurname();

						String holderDni = line[HOLDER_DNI];

						// New Athlete
						AthleteDto athlete;
						if (birthDate == null) {
							athlete = athleteService.findByCode(athleteCode);
						} else {
							athlete = athleteService.findByUniqueColumns(name, firstSurname, secondSurname, birthDate);
						}
						if (athlete == null) {
							athlete = new AthleteDto();
						}
						athlete.setCode(athleteCode);
						athlete.setName(name);
						athlete.setFirstSurname(firstSurname);
						athlete.setSecondSurname(secondSurname);
						athlete.setBirthDate(birthDate);
						athlete.setGender(gender);
						athlete.setDni(dni);
						athlete.setMail(mail);
						athlete.setPhone1(phone1);
						athlete.setPhone2(phone2);
						athlete.setPhone3(phone3);
						athlete.setAddress(address);
						athlete.setPostalCode(postalCode);
						athlete.setMunicipality(municipality);

						// Bank Info
						athlete.setName(name);
						athlete.setIban(iban);
						athlete.setHolderName(holderName);
						athlete.setHolderFirstSurname(holderFirstSurname);
						athlete.setHolderSecondSurname(holderSecondSurname);
						athlete.setHolderDni(holderDni);

						// Familiar Info
						athlete.setFamiliarOneName(fatherName);
						athlete.setFamiliarOneFirstSurname(fatherFirstSurname);
						athlete.setFamiliarOneSecondSurname(fatherSecondSurname);

						athlete.setFamiliarTwoName(motherName);
						athlete.setFamiliarTwoFirstSurname(motherFirstSurname);
						athlete.setFamiliarTwoSecondSurname(motherSecondSurname);

						// Sport School
						if (line[SPORT_SCHOOL] != null && !line[SPORT_SCHOOL].trim().isEmpty()) {
							if (line[SPORT_SCHOOL].equals(STA)) {
								athlete.setSportSchoolId(1);
								athlete.setExtern(false);
							} else if (line[SPORT_SCHOOL].equals(PAR)) {
								athlete.setSportSchoolId(2);
								athlete.setExtern(false);
							} else if (line[SPORT_SCHOOL].equals(EXT)) {
								athlete.setSportSchoolId(3);
								athlete.setExtern(true);
							}
						}
						// New Family
						FamilyDto family = familyService.findByCode(familyCode);
						if (family == null) {
							family = new FamilyDto();
							family.setCode(familyCode);
							family = familyService.save(family);
						}

						athlete.setFamilyId(family.getId());
						athlete = athleteService.save(athlete);

						String registerDate = line[REGISTER_DATE].trim();
						if (line[REGISTER_DATE] != null && !registerDate.isEmpty()
								&& registerDate.split(DATE_SEPARATOR).length == 2) {

							String[] splitDate = registerDate.split(DATE_SEPARATOR);
							String year = splitDate[1].trim();
							String month = registarDateMap.get(splitDate[0].trim());
							HistoricDto historic = new HistoricDto();
							historic.setAthleteId(athlete.getId());
							historic.setType(HistoricType.REGISTER.name());
							try {
								String yearStr = "20";
								if (Integer.parseInt(year) > 50) {
									yearStr = "19";
								}
								historic.setDate(dayFormat.parse(yearStr + year + "-" + month + "-" + "01"));
							} catch (ParseException e) {
								logger.error("Register date");
								logger.error(String.join("_", line));
								logger.error(e.getMessage());
								e.printStackTrace();
							}
							historicService.save(historic);
						}
						String unregisterDate = line[UNREGISTER_DATE].trim();
						if (line[UNREGISTER_DATE] != null && !unregisterDate.isEmpty()
								&& unregisterDate.split(DATE_SEPARATOR).length == 2) {

							String[] splitDate = unregisterDate.split(DATE_SEPARATOR);
							String year = splitDate[1].trim();
							String month = registarDateMap.get(splitDate[0].trim());
							HistoricDto historic = new HistoricDto();
							historic.setAthleteId(athlete.getId());
							historic.setType(HistoricType.UNREGISTER.name());

							try {
								String yearStr = "20";
								if (Integer.parseInt(year) > 50) {
									yearStr = "19";
								}
								historic.setDate(dayFormat.parse(yearStr + year + "-" + month + "-" + "01"));
							} catch (ParseException e) {
								logger.error("Unregister date");
								logger.error(String.join("_", line));
								logger.error(e.getMessage());
								e.printStackTrace();
							}

							historicService.save(historic);
						}
						return athlete;
					} catch (Exception e) {
						logger.error(String.join("_", line));
						logger.error(e.getMessage());
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList());
	}

}
