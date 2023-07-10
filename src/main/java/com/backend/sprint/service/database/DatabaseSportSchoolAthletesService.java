package com.backend.sprint.service.database;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.FamilyDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.HistoricDto;
import com.backend.sprint.model.dto.NameDataDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.utils.HistoricType;
import com.backend.sprint.utils.LicenseType;
import com.opencsv.CSVReader;

@Service
public class DatabaseSportSchoolAthletesService extends DatabaseService {

	private static final int AUTH_IMG = 0;
	private static final int FAMILY_CODE = 1;
	private static final int ATHLETE_CODE = 2;
	private static final int ATHLETE = 3;
	private static final int ATHLETE_FIRSTSURNAME = 4;
	private static final int ATHLETE_SECONDSURNAME = 5;
	private static final int ATHLETE_NAME = 6;
	private static final int BIRTHDATE = 7;
	private static final int YEAR = 8;
	private static final int AGE = 9;
	private static final int GENDER = 10;
	private static final int DNI = 11;
	private static final int PHONE_1 = 12;
	private static final int PHONE_2 = 13;
	private static final int PHONE_3 = 14;
	private static final int ADDRESS = 15;
	private static final int POSTCAL_CODE = 16;
	private static final int MUNICIPALITY = 17;
	private static final int MAIL = 18;
	private static final int FATHER_MAIL = 19;
	private static final int MOTHER_MAIL = 20;
	private static final int FATHER_NAME = 21;
	private static final int MOTHER_NAME = 22;
	private static final int HOLDER_NAME = 23;
	private static final int HOLDER_DNI = 24;
	private static final int IBAN = 25;
	private static final int CATEGORY = 26;
	private static final int LICENSE = 27;
	private static final int LICENSE_TYPE = 28;
	private static final int DORSAL_CATEGORY = 29;
	private static final int DORSAL = 30;
	private static final int GROUP_NAME = 31;

	private static final int MONDAY = 32;
	private static final int TUESDAY = 33;
	private static final int WEDNESDAY = 34;
	private static final int THURSDAY = 35;
	private static final int FRIDAY = 36;
	private static final int SATURDAY = 37;

	private static final int DAYS = 38;
	private static final int NUM_DAYS = 39;
	private static final int SCHEDULE = 40;
	private static final int PAYMENT_TYPE = 41;
	private static final int FEE_TYPE = 42;

	private static final int OBSERVATIONS_SEASON = 49;
	private static final int OBSERVATIONS_1_PAYMENT = 51;
	private static final int OBSERVATIONS_1T_1 = 53;
	private static final int OBSERVATIONS_1T_2 = 55;
	private static final int OBSERVATIONS_LICENCIA = 57;
	private static final int OBSERVATIONS_2T_1 = 59;
	private static final int OBSERVATIONS_2T_2 = 61;
	private static final int OBSERVATIONS_3T_1 = 63;
	private static final int OBSERVATIONS_3T_2 = 65;

	private static final int REGISTER_DATE = 67;

	private static final String DATE_SEPARATOR = "-";

	private static final String[] string_fields_to_check = { "familyCode", "code", "dni",
//			"gender", 
			"nationality", "birthPlace", "familiarOneName", "familiarOneFirstSurname", "familiarOneSecondSurname",
			"familiarOneDni", "familiarOneMail", "familiarTwoName", "familiarTwoFirstSurname",
			"familiarTwoSecondSurname", "familiarTwoDni", "familiarTwoMail",
//			"category", 
			"dorsalCategory", "license", "licenseType", "mail", "phone1", "phone2", "phone3", "municipality",
			"postalCode", "address", "iban", "paymentType", "feeType", "holderName", "holderFirstSurname",
			"holderSecondSurname", "holderDni",
//			"groupId",
			"dorsalNumber" }; //

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	private Logger logger = LoggerFactory.getLogger(DatabaseSportSchoolAthletesService.class);

	public List<AthleteDto> dbFillAtheltesData(int sportSchoolId, String csvName) throws IOException, ParseException {

		SportSchoolDto sportSchool = sportSchoolService.findById(sportSchoolId);

		Long[] athleteTestList = { 14472L, 15263L, 25981L, 27442L, 26372L, 26491L, 28681L, 29791L, 31361L };

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();

		ProfileCredentialsProvider creds = new ProfileCredentialsProvider("davher890");
//		DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();
		Regions region = Regions.US_EAST_1;
		AmazonS3 s3Client = AmazonS3Client.builder().withRegion(region).withCredentials(creds).build();
		s3Client.getObject(new GetObjectRequest("sprint-database", csvName), new File("/tmp/" + csvName));

		CSVReader csvReader = readFile("/tmp/" + csvName);
		return StreamSupport.stream(csvReader.spliterator(), true)
				.filter(d -> Arrays.asList(athleteTestList).contains(Long.valueOf(d[ATHLETE_CODE].trim())))
				.map(line -> {

					try {

						boolean authImg = (line[AUTH_IMG].trim().toUpperCase().equals("X")
								|| line[AUTH_IMG].trim().equals("SI")) ? true : false;
						long familyCode = Long.valueOf(line[FAMILY_CODE].trim());
						long athleteCode = Long.valueOf(line[ATHLETE_CODE].trim());
						String firstSurname = line[ATHLETE_FIRSTSURNAME].trim();
						String secondSurname = line[ATHLETE_SECONDSURNAME].trim();
						String name = line[ATHLETE_NAME].trim();
						LocalDate birthDate = null;
						if (!line[BIRTHDATE].trim().isEmpty() && !line[BIRTHDATE].trim().isEmpty()) {
							birthDate = LocalDate.parse(line[BIRTHDATE].trim(), dateFormat);
						}
						String birthDateYear = line[YEAR];
						String gender = line[GENDER].trim().equals("Hombre") ? "male" : "female";
						String dni = line[DNI].trim();
						String category = line[CATEGORY].trim();
						String license = line[LICENSE].trim();
						String licenseType = line[LICENSE_TYPE].trim().equals("T") ? LicenseType.Territorial.label
								: LicenseType.National.label;
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

						athlete.setUpdatedById(user.getId());

						// Group
						String groupName = line[GROUP_NAME].trim();
						GroupDto group = groupService.findByNameAndSportSchool(groupName, sportSchoolId);

						if (group != null) {
							athlete.setGroupId(group.getId());
							Set<Long> groupSchedules = filterGroupSchedules(line, group.getSchedules());
							athlete.setScheduleIds(groupSchedules);
						}

						try {
							athlete = athleteService.save(athlete);
						} catch (DataIntegrityViolationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// Historic
						String registerDate = line[REGISTER_DATE];
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

						// Observations
						saveObservation(getObservation(line, OBSERVATIONS_SEASON, null, athlete.getId(), user.getId()));
						saveObservation(getObservation(line, OBSERVATIONS_1_PAYMENT, "2021-10-19", athlete.getId(),
								user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_1T_1, "2021-11-19", athlete.getId(), user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_1T_2, "2022-01-11", athlete.getId(), user.getId()));
						saveObservation(getObservation(line, OBSERVATIONS_LICENCIA, "2022-02-03", athlete.getId(),
								user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_2T_1, "2022-02-15", athlete.getId(), user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_2T_2, "2022-03-30", athlete.getId(), user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_3T_1, "2022-05-10", athlete.getId(), user.getId()));
						saveObservation(
								getObservation(line, OBSERVATIONS_3T_2, "2022-06-15", athlete.getId(), user.getId()));

						return athlete;
					} catch (Exception e) {
						logger.error(String.join("_", line));
						logger.error(e.getMessage());
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList());
	}

	private Set<Long> filterGroupSchedules(String[] line, Set<ScheduleDto> schedules) {
		return schedules.stream().filter(scheduleDto -> {
			return line[MONDAY] != null && !line[MONDAY].isEmpty() && line[MONDAY].toUpperCase().equals("X")
					&& scheduleDto.getDayNumber() == 1
					|| line[TUESDAY] != null && !line[TUESDAY].isEmpty() && line[TUESDAY].toUpperCase().equals("X")
							&& scheduleDto.getDayNumber() == 2
					|| line[WEDNESDAY] != null && !line[WEDNESDAY].isEmpty()
							&& line[WEDNESDAY].toUpperCase().equals("X") && scheduleDto.getDayNumber() == 3
					|| line[THURSDAY] != null && !line[THURSDAY].isEmpty() && line[THURSDAY].toUpperCase().equals("X")
							&& scheduleDto.getDayNumber() == 4
					|| line[FRIDAY] != null && !line[FRIDAY].isEmpty() && line[FRIDAY].toUpperCase().equals("X")
							&& scheduleDto.getDayNumber() == 5
					|| line[SATURDAY] != null && !line[SATURDAY].isEmpty() && line[SATURDAY].toUpperCase().equals("X")
							&& scheduleDto.getDayNumber() == 6;
		}).map(ScheduleDto::getId).collect(Collectors.toSet());
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

	public void checkSportSchoolIdAthletes(long sportSchoolId, List<AthleteDto> fileAthletes) {
		List<AthleteDto> findBySportSchoolId = registeredAthleteService.findBySportSchoolId(sportSchoolId);
		Supplier<Stream<AthleteDto>> sportSchoolAthletes = () -> {
			return findBySportSchoolId.parallelStream();
		};
		fileAthletes.parallelStream().forEach(fileAthlete -> {

			NameDataDto holderNameData = getNameDataFromString(fileAthlete.getHolderName());
			NameDataDto familiar1NameData = getNameDataFromString(fileAthlete.getFamiliarOneName());
			NameDataDto familiar2NameData = getNameDataFromString(fileAthlete.getFamiliarTwoName());

			fileAthlete.setHolderName(holderNameData.getName());
			fileAthlete.setHolderFirstSurname(holderNameData.getFirstSurname());
			fileAthlete.setHolderSecondSurname(holderNameData.getSecondSurname());

			fileAthlete.setFamiliarOneName(familiar1NameData.getName());
			fileAthlete.setFamiliarOneFirstSurname(familiar1NameData.getFirstSurname());
			fileAthlete.setFamiliarOneSecondSurname(familiar1NameData.getSecondSurname());

			fileAthlete.setFamiliarTwoName(familiar2NameData.getName());
			fileAthlete.setFamiliarTwoFirstSurname(familiar2NameData.getFirstSurname());
			fileAthlete.setFamiliarTwoSecondSurname(familiar2NameData.getSecondSurname());

			Optional<AthleteDto> anyAthlete = sportSchoolAthletes.get()
					.filter(athlete -> athlete.getName().equals(fileAthlete.getName().trim())
							&& athlete.getFirstSurname().equals(fileAthlete.getFirstSurname().trim())
							&& athlete.getSecondSurname().equals(fileAthlete.getSecondSurname().trim()))
					.findFirst();
			if (!anyAthlete.isPresent()) {
				System.out.println(fileAthlete.getFamilyCode() + ";" + fileAthlete.getCode() + ";"
						+ fileAthlete.getName() + ";" + fileAthlete.getFirstSurname() + ";"
						+ fileAthlete.getSecondSurname() + "; El atleta no se encontro en la plataforma");
			} else {
				AthleteDto athlete = anyAthlete.get();

				try {
					for (String field : string_fields_to_check) {
						checkAthleteField(athlete, fileAthlete, field);
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void checkAthleteField(AthleteDto dbAthlete, AthleteDto fileAthlete, String field)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Object dbValue = PropertyUtils.getProperty(dbAthlete, field);
		Object fileValue = PropertyUtils.getProperty(fileAthlete, field);

		try {

			if (dbValue instanceof String) {
				String dbValueStr = (String) dbValue;
				String fileValueStr = (String) fileValue;
				if (dbValueStr == null || dbValueStr.isEmpty()) {
					if (fileValueStr != null && !fileValueStr.isEmpty()) {

						printErrorMessage(dbAthlete, fileAthlete, field, fileValueStr, dbValueStr);
					}
				} else {
					if (fileValueStr != null && !fileValueStr.isEmpty()) {
						if (!dbValueStr.toLowerCase().trim().replace(",", "")
								.equals(fileValueStr.toLowerCase().trim().replace(",", ""))) {
							printErrorMessage(dbAthlete, fileAthlete, field, fileValueStr, dbValueStr);
						}
					}
				}
			}
			if (dbValue instanceof Long) {
				Long dbValueLong = (Long) PropertyUtils.getProperty(fileAthlete, field);
				Long fileValueLong = (Long) PropertyUtils.getProperty(fileAthlete, field);

				if (dbValueLong == null) {
					if (fileValueLong != null) {
						printErrorMessage(dbAthlete, fileAthlete, field, fileValueLong, dbValueLong);
					}
				} else {
					if (fileValueLong != null) {
						if (dbValueLong.longValue() != fileValueLong.longValue()) {
							printErrorMessage(dbAthlete, fileAthlete, field, fileValueLong, dbValueLong);
						}
					}
				}
			}
		} catch (NullPointerException e) {
			System.out.println("Checking -" + dbValue + "- -" + fileValue + "-");
		}
	}

	private void printErrorMessage(AthleteDto dbAthlete, AthleteDto fileAthlete, String field, Object fileValueStr,
			Object dbValueStr) {
		System.out.println(dbAthlete.getFamilyCode() + ";" + dbAthlete.getCode() + ";" + fileAthlete.getName() + ";"
				+ fileAthlete.getFirstSurname() + ";" + fileAthlete.getSecondSurname() + ";" + field + ";" + field
				+ " del excel: " + fileValueStr + ", no coincide con el de la plataforma: " + dbValueStr);
	}
}
