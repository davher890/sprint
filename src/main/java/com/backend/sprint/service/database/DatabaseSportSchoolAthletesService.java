package com.backend.sprint.service.database;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.NameDataDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.FamilyService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.HistoricService;
import com.backend.sprint.service.SportSchoolService;
import com.backend.sprint.service.TrainerService;
import com.backend.sprint.utils.HistoricType;
import com.backend.sprint.utils.LicenseType;
import com.opencsv.CSVReader;

@Service
public class DatabaseSportSchoolAthletesService extends DatabaseService {

	private static final int AUTH_IMG = 0;
	private static final int FAMILY_CODE = 1;
	private static final int ATHLETE_CODE = 2;
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
	private static final int ADDRESS = 14;
	private static final int POSTCAL_CODE = 15;
	private static final int MUNICIPALITY = 16;
	private static final int FATHER_MAIL = 17;
	private static final int MOTHER_MAIL = 18;
	private static final int MAIL = 19;
	private static final int FATHER_NAME = 20;
	private static final int MOTHER_NAME = 21;
	private static final int HOLDER_NAME = 22;
	private static final int HOLDER_DNI = 23;
	private static final int IBAN = 24;
	private static final int CATEGORY = 25;
	private static final int LICENSE = 26;
	private static final int LICENSE_TYPE = 27;
	private static final int DORSAL_CATEGORY = 28;
	private static final int DORSAL = 29;
	private static final int GROUP_NAME = 31;
	private static final int GROUP_TYPE = 32;
	private static final int DAYS = 33;
	private static final int NUM_DAYS = 34;
	private static final int SCHEDULE = 35;
	private static final int PAYMENT_TYPE = 36;
	private static final int FEE_TYPE = 37;
	private static final int SANTA_ANA_OBSERVATIONS = 45;
	private static final int PARACUELLOS_OBSERVATIONS = 43;

	private static final int PARACUELLOS_REGISTER_DATE = 61;
	private static final int SANTA_ANA_REGISTER_DATE = 63;

	private static final String DATE_SEPARATOR = "-";

	private Logger logger = LoggerFactory.getLogger(DatabaseSportSchoolAthletesService.class);

	@Autowired
	private FamilyService familyService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private TrainerService trainerService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SportSchoolService sportSchoolService;

	@Autowired
	private HistoricService historicService;

	public List<AthleteDto> dbFillAtheltesData(int sportSchoolId, String csvName) throws IOException, ParseException {

		SportSchoolDto sportSchool = sportSchoolService.findById(sportSchoolId);
		String fileName = System.getProperty("user.dir") + "/src/main/resources/" + csvName;

		Long[] athleteTestList = { 29241L, 29251L, 29261L, 29271L, 29281L, 29291L, 29301L, 29321L, 29331L, 29491L,
				29501L, 29521L, 29531L, 29532L, 29533L, 29671L, 29681L };

		CSVReader csvReader = readFile(fileName);
		return StreamSupport.stream(csvReader.spliterator(), true)
//				.filter(d -> Arrays.asList(athleteTestList).contains(Long.valueOf(d[ATHLETE_CODE].trim())))
				.map(line -> {

					try {

						boolean authImg = (line[AUTH_IMG].trim().toUpperCase().equals("X")
								|| line[AUTH_IMG].trim().equals("SI")) ? true : false;
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
						if (!line[BIRTHDATE].trim().isEmpty() && !line[BIRTHDATE].trim().isEmpty()) {
							birthDate = dateFormat.parse(line[BIRTHDATE].trim());
						}
						String birthDateYear = line[YEAR];
						String gender = line[GENDER].trim().equals("Hombre") ? "male" : "female";
						String dni = line[DNI].trim();
						String category = line[CATEGORY].trim();
						String license = line[LICENSE].trim();
						String licenseType = line[LICENSE_TYPE].trim().equals("T") ? LicenseType.Territorial.name()
								: LicenseType.National.name();
						String dorsalCategory = line[DORSAL_CATEGORY].trim();
						String dorsalStr = line[DORSAL].trim();
						long dorsalNumber = 0L;
						if (!dorsalStr.isEmpty()) {
							dorsalNumber = Long.valueOf(dorsalStr);
						}
						String phone1 = line[PHONE_1].trim();
						String phone2 = line[PHONE_2].trim();
						String phone3 = line[PHONE_3].trim();
						String address = line[ADDRESS].trim();
						String postalCode = line[POSTCAL_CODE].trim();
						String municipality = line[MUNICIPALITY].trim();

						String fatherMail = line[FATHER_MAIL].trim();
						String motherMail = line[MOTHER_MAIL].trim();
						String mail = line[MAIL].trim();

						NameDataDto father = getNameData(line, FATHER_NAME);
						String fatherName = father.getName();
						String fatherFirstSurname = father.getFirstSurname();
						String fatherSecondSurname = father.getSecondSurname();

						NameDataDto mother = getNameData(line, MOTHER_NAME);
						String motherName = mother.getName();
						String motherFirstSurname = mother.getFirstSurname();
						String motherSecondSurname = mother.getSecondSurname();

						String feeType = line[FEE_TYPE].trim();
						String iban = line[IBAN].trim();

						NameDataDto holder = getNameData(line, HOLDER_NAME);
						String holderName = holder.getName();
						String holderFirstSurname = holder.getFirstSurname();
						String holderSecondSurname = holder.getSecondSurname();
						String holderDni = line[HOLDER_DNI].trim();

						String paymentType = line[PAYMENT_TYPE].trim();

						String observations = line[sportSchoolId == 1 ? SANTA_ANA_OBSERVATIONS
								: PARACUELLOS_OBSERVATIONS].trim();

						// New Athlete
						AthleteDto athlete;
						athlete = athleteService.findByCode(athleteCode);
						if (athlete == null) {
							athlete = new AthleteDto();
						}
						athlete.setImageAuth(authImg);
						athlete.setCode(athleteCode);
						athlete.setName(name);
						athlete.setFirstSurname(firstSurname);
						athlete.setSecondSurname(secondSurname);
						athlete.setBirthDate(birthDate);
						athlete.setGender(gender);
						athlete.setDni(dni);
						athlete.setLicense(license);
						athlete.setLicenseType(licenseType);
						athlete.setCategory(category);
						athlete.setDorsalNumber(dorsalNumber);
						athlete.setDorsalCategory(dorsalCategory);
						athlete.setMail(mail);
						athlete.setPhone1(phone1);
						athlete.setPhone2(phone2);
						athlete.setPhone3(phone3);
						athlete.setAddress(address);
						athlete.setPostalCode(postalCode);
						athlete.setMunicipality(municipality);

						// Bank Info
						athlete.setName(name);
						athlete.setFeeType(feeType);
						athlete.setIban(iban);
						athlete.setPaymentType(paymentType);
						athlete.setHolderName(holderName);
						athlete.setHolderFirstSurname(holderFirstSurname);
						athlete.setHolderSecondSurname(holderSecondSurname);
						athlete.setHolderDni(holderDni);
						// athlete.setNumDays(numDays);

						// Familiar Info
						athlete.setFamiliarOneName(fatherName);
						athlete.setFamiliarOneFirstSurname(fatherFirstSurname);
						athlete.setFamiliarOneSecondSurname(fatherSecondSurname);
						athlete.setFamiliarOneMail(fatherMail);

						athlete.setFamiliarTwoName(motherName);
						athlete.setFamiliarTwoFirstSurname(motherFirstSurname);
						athlete.setFamiliarTwoSecondSurname(motherSecondSurname);
						athlete.setFamiliarTwoMail(motherMail);

						// Sport School
						athlete.setSportSchoolId(sportSchool.getId());

						// New Family
						FamilyDto family = familyService.findByCode(familyCode);
						if (family == null) {
							family = new FamilyDto();
							family.setCode(familyCode);
							family = familyService.save(family);
						}
						athlete.setFamilyId(family.getId());

						// Group
						String groupName = line[GROUP_NAME].trim();
						GroupDto group = groupService.findByNameAndSportSchool(groupName, sportSchoolId);

						if (group != null) {
							athlete.setGroupId(group.getId());
							athlete.setScheduleIds(group.getScheduleIds());
						}

						try {
							athlete = athleteService.save(athlete);
						} catch (DataIntegrityViolationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// Historic
						String registerDate = line[sportSchoolId == 1 ? SANTA_ANA_REGISTER_DATE
								: PARACUELLOS_REGISTER_DATE];
						if (registerDate != null && !registerDate.trim().isEmpty()
								&& registerDate.trim().split(DATE_SEPARATOR).length == 2) {

							String[] splitDate = registerDate.trim().split(DATE_SEPARATOR);
							String year = splitDate[1].trim();
							String month = registarDateMap.get(splitDate[0].trim().toLowerCase());
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
						return athlete;
					} catch (Exception e) {
						logger.error(String.join("_", line));
						logger.error(e.getMessage());
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList());
	}

	public Set<SportSchoolDto> dbFillSportSchoolsData() {

		List<String> ssNames = Arrays.asList(new String[] { "Santa Ana", "Paracuellos", "Externo" });

		return ssNames.stream().map(name -> {
			SportSchoolDto dto = sportSchoolService.findByName(name);
			if (dto == null) {
				dto = new SportSchoolDto();
			}
			dto.setName(name);
			return sportSchoolService.save(dto);
		}).collect(Collectors.toSet());
	}

	public Set<TrainerDto> dbFillTrainersData() {

		List<String> trainerNames = Arrays.asList(new String[] { "WALTER", "SANTAMARÍA", "SANDRA", "PACO", "NOELIA",
				"MAURICIO", "LAURA", "KATE", "JULIAN", "JOSE", "JESUS", "JAVI", "IRENE", "DAVID", "DANI", "ANGEL",
				"ANAIS", "ANA", "ALVARO" });

		return trainerNames.stream().map(name -> {
			TrainerDto dto = trainerService.findByName(name);
			if (dto == null) {
				dto = new TrainerDto();
			}
			dto.setName(name);
			return trainerService.save(dto);
		}).collect(Collectors.toSet());
	}
}
