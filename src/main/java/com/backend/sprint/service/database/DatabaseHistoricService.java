package com.backend.sprint.service.database;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
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
	private static final String PAD = "PAD";

	private static final int FAMILY_CODE = 0;
	private static final int ATHLETE_CODE = 1;
	private static final int SPORT_SCHOOL = 2;
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
	private static final int ADDREES = 15;
	private static final int POSTAL_CODE = 16;
	private static final int MUNICIPALITY = 17;
	private static final int MAIL = 18;
	private static final int FATHER_MAIL = 19;
	private static final int MOTHER_MAIL = 20;
	private static final int FATHER_NAME = 21;
	private static final int MOTHER_NAME = 22;
	private static final int HOLDER_NAME = 23;
	private static final int HOLDER_DNI = 24;
	private static final int IBAN = 25;
	private static final int OBSERVATIONS = 26;
	private static final int REGISTER_DATE = 27;
	private static final int UNREGISTER_DATE = 28;

	private static final String DATE_SEPARATOR = "-";

	private Logger logger = LoggerFactory.getLogger(DatabaseHistoricService.class);

	@Autowired
	private FamilyService familyService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private HistoricService historicService;

	public List<AthleteDto> dbFillAtheltesData(String csvName) throws IOException, ParseException {

		Long[] athleteTestList = { 17461L };

//		ProfileCredentialsProvider creds = new ProfileCredentialsProvider("davher890");
		DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();
		Regions region = Regions.US_EAST_1;
		AmazonS3 s3Client = AmazonS3Client.builder().withRegion(region).withCredentials(creds).build();
		s3Client.getObject(new GetObjectRequest("sprint-database", csvName), new File("/tmp/" + csvName));

		CSVReader csvReader = readFile("/tmp/" + csvName);
		return StreamSupport.stream(csvReader.spliterator(), false)
//				.filter(d -> Arrays.asList(athleteTestList).contains(Long.valueOf(d[ATHLETE_CODE].trim())))
				.map(line -> {
					try {

						long familyCode = Long.valueOf(line[FAMILY_CODE].trim());

						long athleteCode = Long.valueOf(line[ATHLETE_CODE].trim());
						String firstSurname = line[ATHLETE_FIRSTSURNAME].trim();
						String secondSurname = line[ATHLETE_SECONDSURNAME].trim();
						String name = line[ATHLETE_NAME].trim();

						LocalDate birthDate = null;
						if (!line[BIRTHDATE].isEmpty() && !line[BIRTHDATE].trim().isEmpty()) {
							birthDate = LocalDate.parse(line[BIRTHDATE].trim(), dateFormat);
						}
						String gender = line[GENDER].trim().equals("Hombre") ? "male" : "female";
						String dni = line[DNI].trim();

						String phone1 = line[PHONE_1].trim();
						String phone2 = line[PHONE_2].trim();
						String phone3 = line[PHONE_3].trim();

						String address = line[ADDREES].trim();
						String postalCode = line[POSTAL_CODE].trim();
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

						String iban = line[IBAN].trim();
						if (iban.equals("00000000000000000000")) {
							iban = null;
						}

						NameDataDto holder = getNameData(line, HOLDER_NAME);
						String holderName = holder.getName();
						String holderFirstSurname = holder.getFirstSurname();
						String holderSecondSurname = holder.getSecondSurname();

						String holderDni = line[HOLDER_DNI].trim();

						// New Athlete
						AthleteDto athlete;
						athlete = athleteService.findByCode(athleteCode);
						if (athlete == null) {
							athlete = new AthleteDto();
						}
						athlete.setCode(athleteCode);
						athlete.setName(athlete.getName() == null ? name : athlete.getName());
						athlete.setFirstSurname(
								athlete.getFirstSurname() == null ? firstSurname : athlete.getFirstSurname());
						athlete.setSecondSurname(
								athlete.getSecondSurname() == null ? secondSurname : athlete.getSecondSurname());
						athlete.setBirthDate(athlete.getBirthDate() == null ? birthDate : athlete.getBirthDate());
						athlete.setGender(athlete.getGender() == null ? gender : athlete.getGender());
						athlete.setDni(athlete.getDni() == null ? dni : athlete.getDni());
						athlete.setMail(athlete.getMail() == null ? mail : athlete.getMail());
						athlete.setPhone1(athlete.getPhone1() == null ? phone1 : athlete.getPhone1());
						athlete.setPhone2(athlete.getPhone2() == null ? phone2 : athlete.getPhone2());
						athlete.setPhone3(athlete.getPhone3() == null ? phone3 : athlete.getPhone3());
						athlete.setAddress(athlete.getAddress() == null ? address : athlete.getAddress());
						athlete.setPostalCode(athlete.getPostalCode() == null ? postalCode : athlete.getPostalCode());
						athlete.setMunicipality(
								athlete.getMunicipality() == null ? municipality : athlete.getMunicipality());

						// Bank Info
						athlete.setIban(athlete.getIban() == null ? iban : athlete.getIban());
						athlete.setHolderName(athlete.getHolderName() == null ? holderName : athlete.getHolderName());
						athlete.setHolderFirstSurname(athlete.getHolderFirstSurname() == null ? holderFirstSurname
								: athlete.getHolderFirstSurname());
						athlete.setHolderSecondSurname(athlete.getHolderSecondSurname() == null ? holderSecondSurname
								: athlete.getHolderSecondSurname());
						athlete.setHolderDni(athlete.getHolderDni() == null ? holderDni : athlete.getHolderDni());
						athlete.setFamiliarOneMail(fatherMail);

						// Familiar Info
						athlete.setFamiliarOneName(
								athlete.getFamiliarOneName() == null ? fatherName : athlete.getFamiliarOneName());
						athlete.setFamiliarOneFirstSurname(
								athlete.getFamiliarOneFirstSurname() == null ? fatherFirstSurname
										: athlete.getFamiliarOneFirstSurname());
						athlete.setFamiliarOneSecondSurname(
								athlete.getFamiliarOneSecondSurname() == null ? fatherSecondSurname
										: athlete.getFamiliarOneSecondSurname());
						athlete.setFamiliarOneMail(fatherMail);

						athlete.setFamiliarTwoName(
								athlete.getFamiliarTwoName() == null ? motherName : athlete.getFamiliarTwoName());
						athlete.setFamiliarTwoFirstSurname(
								athlete.getFamiliarTwoFirstSurname() == null ? motherFirstSurname
										: athlete.getFamiliarTwoFirstSurname());
						athlete.setFamiliarTwoSecondSurname(
								athlete.getFamiliarTwoSecondSurname() == null ? motherSecondSurname
										: athlete.getFamiliarTwoSecondSurname());
						athlete.setFamiliarTwoMail(motherMail);

						// Sport School
						if (line[SPORT_SCHOOL] != null && !line[SPORT_SCHOOL].trim().isEmpty()) {
							if (line[SPORT_SCHOOL].equals(STA)) {
								athlete.setSportSchoolId(1);
							} else if (line[SPORT_SCHOOL].equals(PAR)) {
								athlete.setSportSchoolId(2);
							} else if (line[SPORT_SCHOOL].equals(EXT)) {
								athlete.setSportSchoolId(3);
							} else if (line[SPORT_SCHOOL].equals(PAD)) {
								athlete.setSportSchoolId(5);
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
							String month = registarDateMap.get(splitDate[0].trim().toLowerCase());
							HistoricDto historic = new HistoricDto();
							historic.setAthleteId(athlete.getId());
							historic.setType(HistoricType.REGISTER.name());
							String yearStr = "20";
							if (Integer.parseInt(year) > 50) {
								yearStr = "19";
							}
							historic.setDate(LocalDate.parse(yearStr + year + "-" + month + "-" + "01", dayFormatLd));
							historicService.save(historic);
						}
						String unregisterDate = line[UNREGISTER_DATE].trim();
						if (line[UNREGISTER_DATE] != null && !unregisterDate.isEmpty()
								&& unregisterDate.split(DATE_SEPARATOR).length == 2) {

							String[] splitDate = unregisterDate.split(DATE_SEPARATOR);
							String year = splitDate[1].trim();
							String month = registarDateMap.get(splitDate[0].trim().toLowerCase());
							HistoricDto historic = new HistoricDto();
							historic.setAthleteId(athlete.getId());
							historic.setType(HistoricType.UNREGISTER.name());
							historic.setObservations(line[OBSERVATIONS]);

							String yearStr = "20";
							if (Integer.parseInt(year) > 50) {
								yearStr = "19";
							}
							historic.setDate(LocalDate.parse(yearStr + year + "-" + month + "-" + "01", dayFormatLd));

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
