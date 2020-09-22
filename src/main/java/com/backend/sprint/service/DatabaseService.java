package com.backend.sprint.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.utils.LicenseType;
import com.backend.sprint.utils.WeekDays;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
public class DatabaseService {

	private static final int AUTH_IMG = 0;
	private static final int FAMILY_CODE = 1;
	private static final int ATHLETE_CODE = 2;
	private static final int ATHLETE_SURNAMES = 4;
	private static final int ATHLETE_NAME = 5;
	private static final int BIRTHDATE = 6;
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
	private static final int FATHER_NAME = 25;
	private static final int MOTHER_NAME = 26;
	private static final int HOLDER_NAME = 27;
	private static final int HOLDER_DNI = 28;
	private static final int IBAN = 29;
	private static final int GROUP_NAME = 32;

	@Autowired
	private FamilyService familyService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private TrainerService trainerService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SportSchoolService sportSchoolService;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

	@Transactional
	public Set<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {

		String csvFile = "/Users/david/Development/git/sprint/src/main/resources/BD_PARA_PRUEBAS.csv";
		File file = new File(csvFile);
		Reader reader = Files.newBufferedReader(file.toPath());

		CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();

		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(parser).build();

		String[] line;

		SportSchoolDto sportSchool = sportSchoolService.findByName("Santa Ana");

		Set<AthleteDto> athletes = new HashSet<>();
		while ((line = csvReader.readNext()) != null) {
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
			Date birthDate = dateFormat.parse(line[BIRTHDATE].trim());
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

			String address = line[19].trim();
			String postalCode = line[20].trim();
			String municipality = line[21].trim();

			String fatherMail = line[22].trim();
			String motherMail = line[23].trim();
			String mail = line[24].trim();

			String fatherName = null;
			String fatherFirstSurname = null;
			String fatherSecondSurname = null;

			if (line[FATHER_NAME] != null && !line[FATHER_NAME].trim().isEmpty()) {
				String father = line[FATHER_NAME];
				if (father.contains(",")) {

					List<String> splitFather = Arrays.asList(father.split(","));
					fatherName = splitFather.get(1);
					if (splitFather.get(0).split(" ").length > 1) {
						List<String> splirSurname = Arrays.asList(splitFather.get(0).split(" "));
						fatherFirstSurname = splirSurname.get(0);
						fatherSecondSurname = splirSurname.subList(1, splirSurname.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				} else {
					List<String> splitFather = Arrays.asList(father.split(" "));
					fatherName = splitFather.get(0);
					fatherFirstSurname = splitFather.get(1);
					if (splitFather.get(0).split(" ").length > 1) {
						fatherSecondSurname = splitFather.subList(2, splitFather.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				}
			}

			String motherName = null;
			String motherFirstSurname = null;
			String motherSecondSurname = null;

			if (line[MOTHER_NAME] != null && !line[MOTHER_NAME].trim().isEmpty()) {
				String mother = line[MOTHER_NAME];
				if (mother.contains(",")) {
					List<String> splitMother = Arrays.asList(mother.split(","));
					motherName = splitMother.get(1);
					if (splitMother.get(0).split(" ").length > 1) {
						List<String> splirSurname = Arrays.asList(splitMother.get(0).split(" "));
						motherFirstSurname = splirSurname.get(0);
						motherSecondSurname = splirSurname.subList(1, splirSurname.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				} else {
					List<String> splitMother = Arrays.asList(mother.split(" "));
					motherName = splitMother.get(0);
					motherFirstSurname = splitMother.get(1);
					if (splitMother.get(0).split(" ").length > 1) {
						motherSecondSurname = splitMother.subList(2, splitMother.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				}
			}
			String feeType = line[31].trim();
			String iban = line[IBAN].trim();

			String holderName = null;
			String holderFirstSurname = null;
			String holderSecondSurname = null;
			String holder = line[HOLDER_NAME];
			if (holder != null && !holder.trim().isEmpty()) {
				if (holder.contains(",")) {

					List<String> splitHolder = Arrays.asList(holder.split(","));
					holderName = splitHolder.get(1);
					if (splitHolder.get(0).split(" ").length > 1) {
						List<String> splirSurname = Arrays.asList(splitHolder.get(0).split(" "));
						holderFirstSurname = splirSurname.get(0);
						holderSecondSurname = splirSurname.subList(1, splirSurname.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				} else {
					List<String> splitHolder = Arrays.asList(holder.split(" "));
					holderName = splitHolder.get(0);
					holderFirstSurname = splitHolder.get(1);
					if (splitHolder.get(0).split(" ").length > 1) {
						holderSecondSurname = splitHolder.subList(2, splitHolder.size()).stream()
								.reduce((identity, accumulator) -> {
									return identity.trim() + " " + accumulator.trim();
								}).orElse(null);
					}
				}
			}

			String holderDni = line[HOLDER_DNI];

			String paymentType = line[30].trim();
			int numDays = Integer.parseInt(line[34].trim().isEmpty() ? "0" : line[34].trim().substring(0, 1));

			// New Family
			// FamilyDto family = familyService.findByCode(familyCode);
			// if (family == null) {
			// family = new FamilyDto();
			// family.setCode(familyCode);
			// }
			// family.setFamiliarOneSurname(firstSurname);
			// family.setFamiliarTwoSurname(secondSurname);
			// family.setFamiliarOneMail(fatherMail);
			// family.setFamiliarTwoMail(motherMail);
			// family = familyService.save(family);

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
			// athlete.setFamilyId(family.getId());

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

			// FAmiliar Info
			athlete.setFamiliarOneName(fatherName);
			athlete.setFamiliarOneFirstSurname(fatherFirstSurname);
			athlete.setFamiliarOneSecondSurname(fatherSecondSurname);
			athlete.setFamiliarOneMail(fatherMail);

			athlete.setFamiliarTwoName(motherName);
			athlete.setFamiliarTwoFirstSurname(motherFirstSurname);
			athlete.setFamiliarTwoSecondSurname(motherSecondSurname);
			athlete.setFamiliarTwoMail(motherMail);

			athlete.setFamilyCode(familyCode);

			// Sport School
			athlete.setSportSchoolId(sportSchool.getId());

			// Group
			String groupName = line[GROUP_NAME];
			GroupDto group = groupService.findByName(groupName);

			if (group != null) {
				athlete.setGroupId(group.getId());
				athlete.setScheduleIds(group.getScheduleIds());
			}

			if (athleteService.findByCode(athlete.getCode()) != null) {
				athlete.setId(athleteService.findByCode(athlete.getCode()).getId());
			}
			final AthleteDto athleteFinal = athleteService.save(athlete);
			athletes.add(athleteFinal);

		}

		reader.close();
		csvReader.close();
		return athletes;
	}

	public Set<GroupDto> dbFillGroupsData() throws IOException, ParseException {

		String csvFile = "/Users/david/Development/git/sprint/src/main/resources/BD_PARA_PRUEBAS.csv";
		File file = new File(csvFile);
		Reader reader = Files.newBufferedReader(file.toPath());

		CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();

		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(parser).build();

		String[] line;

		Set<GroupDto> groups = new HashSet<>();
		while ((line = csvReader.readNext()) != null) {
			// Group
			String groupName = line[32];
			String weekDay = line[33];
			String schedule = line[35];

			groups.add(createGroup(groupName, weekDay, schedule));
		}
		return groups;
	}

	private GroupDto createGroup(String groupName, String weekDay, String schedule) {

		GroupDto group = groupService.findByName(groupName);
		if (group == null) {
			group = new GroupDto();
		}
		group.setName(groupName);
		group.setScheduleIds(new HashSet<Long>());
		group.setSportSchoolId(sportSchoolService.findByName("Santa Ana").getId());

		String option1 = "L -X 17:45- 19:15, J 19:15 - 20:45";
		String option2 = "L 18:00- 19:30, M y J 19:00 - 20:30";
		String option3 = "M -J 19:00-20:30 y V 17:00-18:30";

		String scheduleTrim = schedule.trim();
		if (scheduleTrim.equals(option1.trim())) {
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.MONDAY.name(), 17, 45, 19, 15).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.WEDNESDAY.name(), 17, 45, 19, 15).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.THURSDAY.name(), 19, 15, 20, 45).getId());
		}

		else if (scheduleTrim.equals(option2.trim())) {
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.MONDAY.name(), 18, 0, 19, 30).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.TUESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.THURSDAY.name(), 19, 0, 20, 30).getId());
		}

		else if (scheduleTrim.equals(option3.trim())) {
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.TUESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.THURSDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(scheduleService.findByTime(WeekDays.FRIDAY.name(), 17, 0, 18, 30).getId());
		}

		else {
			List<String> days = Arrays.asList(weekDay.replace("y", ",").split(","));
			group.getScheduleIds().addAll(days.stream().map(dayStr -> {
				int startHour = Integer.parseInt(scheduleTrim.split("-")[0].split(":")[0].trim());
				int startMinute = Integer.parseInt(scheduleTrim.split("-")[0].split(":")[1].trim());

				int endHour = Integer.parseInt(scheduleTrim.split("-")[1].split(":")[0].trim());
				int endMinute = Integer.parseInt(scheduleTrim.split("-")[1].split(":")[1].trim());

				ScheduleDto id = scheduleService.findByTime(translateWeekDay(dayStr).name(), startHour, startMinute,
						endHour, endMinute);
				return id.getId();
			}).collect(Collectors.toSet()));
		}
		return groupService.save(group);
	}

	public Set<ScheduleDto> dbFillSchedulesData() throws IOException, ParseException {

		return createSchedules();
	}

	private Set<ScheduleDto> createSchedules() {

		Set<ScheduleDto> schedules = new HashSet<>();

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.TUESDAY.name(), 17, 0, 18, 0)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 17, 0, 18, 0)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.TUESDAY.name(), 17, 0, 18, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 17, 0, 18, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.FRIDAY.name(), 17, 0, 18, 30)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 17, 30, 18, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.TUESDAY.name(), 17, 30, 18, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.WEDNESDAY.name(), 17, 30, 18, 30)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 17, 30, 19, 0)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.WEDNESDAY.name(), 17, 30, 19, 0)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 17, 30, 19, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.WEDNESDAY.name(), 17, 30, 19, 30)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 18, 0, 19, 0)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 18, 0, 19, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.TUESDAY.name(), 18, 0, 19, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 18, 0, 19, 30)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 19, 0, 20, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.TUESDAY.name(), 19, 0, 20, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.WEDNESDAY.name(), 19, 0, 20, 30)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 19, 0, 20, 30)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.MONDAY.name(), 17, 45, 19, 15)));
		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.WEDNESDAY.name(), 17, 45, 19, 15)));

		schedules.add(scheduleService.save(new ScheduleDto(WeekDays.THURSDAY.name(), 19, 15, 20, 45)));

		return schedules;
	}

	private WeekDays translateWeekDay(String day) {

		if (day.trim().toLowerCase().equals("lunes")) {
			return WeekDays.MONDAY;
		}
		if (day.trim().toLowerCase().equals("martes")) {
			return WeekDays.TUESDAY;
		}
		if (day.trim().toLowerCase().equals("miercoles")) {
			return WeekDays.WEDNESDAY;
		}
		if (day.trim().toLowerCase().equals("jueves")) {
			return WeekDays.THURSDAY;
		}
		if (day.trim().toLowerCase().equals("viernes")) {
			return WeekDays.FRIDAY;
		} else {
			return null;
		}
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
