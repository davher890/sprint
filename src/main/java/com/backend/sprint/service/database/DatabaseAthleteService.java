package com.backend.sprint.service.database;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.backend.sprint.utils.LicenseType;
import com.opencsv.CSVReader;

@Service
public class DatabaseAthleteService extends DatabaseService {

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
	private static final int CATEGORY = 11;
	private static final int LICENSE = 12;
	private static final int LICENSE_TYPE = 13;
	private static final int DORSAL_CATEGORY = 14;
	private static final int DORSAL = 15;
	private static final int PHONE_1 = 16;
	private static final int PHONE_2 = 17;
	private static final int PHONE_3 = 18;
	private static final int ADDRESS = 19;
	private static final int POSTCAL_CODE = 20;
	private static final int MUNICIPALITY = 21;
	private static final int FATHER_MAIL = 22;
	private static final int MOTHER_MAIL = 23;
	private static final int MAIL = 24;
	private static final int FATHER_NAME = 25;
	private static final int MOTHER_NAME = 26;
	private static final int HOLDER_NAME = 27;
	private static final int HOLDER_DNI = 28;
	private static final int IBAN = 29;
	private static final int PAYMENT_TYPE = 30;
	private static final int FEE_TYPE = 31;
	private static final int GROUP_NAME = 32;
	private static final int DAYS = 33;
	private static final int NUM_DAYS = 34;
	private static final int SCHEDULE = 35;
	private static final int REGISTER_DATE = 61;
	private static final int UNREGISTER_DATE = 62;

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

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

	@Transactional
	public List<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {

		String fileName = "/Users/david/Development/git/sprint/src/main/resources/BD_PARA_PRUEBAS.csv";

		CSVReader csvReader = readFile(fileName);
		SportSchoolDto sportSchool = sportSchoolService.findByName("Santa Ana");
		return StreamSupport.stream(csvReader.spliterator(), false).map(line -> {

			System.out.println(String.join("_", line));

			boolean authImg = line[AUTH_IMG].trim().equals("SI") ? true : false;
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
			if (!line[BIRTHDATE].trim().isEmpty()) {
				try {
					birthDate = dateFormat.parse(line[BIRTHDATE].trim());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
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
			String holderDni = line[HOLDER_DNI];

			String paymentType = line[PAYMENT_TYPE].trim();

			// New Athlete
			AthleteDto athlete = athleteService.findByCode(athleteCode);
			if (athlete == null) {
				athlete = new AthleteDto();
				athlete.setCode(athleteCode);
			}
			athlete.setImageAuth(authImg);
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
			athlete.setExtern(false);
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
			String groupName = line[GROUP_NAME];
			GroupDto group = groupService.findByName(groupName);

			if (group != null) {
				athlete.setGroupId(group.getId());
				athlete.setScheduleIds(group.getScheduleIds());
			}

			athlete = athleteService.save(athlete);

			// Historic
			if (line[REGISTER_DATE] != null && !line[REGISTER_DATE].isEmpty()
					&& line[REGISTER_DATE].split(".-").length == 2) {

				String[] splitDate = line[REGISTER_DATE].split(".-");
				String year = splitDate[1];
				String month = registarDateMap.get(splitDate[0]);
				HistoricDto historic = new HistoricDto();
				historic.setAthleteId(athlete.getId());
				historic.setType("REGISTER");
				try {
					historic.setDate(dayFormat.parse("20" + year + "-" + month + "-" + "01"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				historicService.save(historic);
			}
			if (line[UNREGISTER_DATE] != null && !line[UNREGISTER_DATE].isEmpty()
					&& line[UNREGISTER_DATE].split(".-").length == 2) {

				String[] splitDate = line[UNREGISTER_DATE].split(".-");
				String year = splitDate[1];
				String month = registarDateMap.get(splitDate[0]);
				HistoricDto historic = new HistoricDto();
				historic.setAthleteId(athlete.getId());
				historic.setType("UNREGISTER");
				try {
					historic.setDate(dayFormat.parse("20" + year + "-" + month + "-" + "01"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				historicService.save(historic);
			}

			return athlete;
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
