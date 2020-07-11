package com.backend.sprint.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.FamilyDto;
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
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@Autowired
	private SportSchoolService sportSchoolService;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

	@Transactional
	public List<AthleteDto> dbFillAtheltesData() throws IOException, ParseException {

		String csvFile = "/Users/david/Development/git/sprint/src/main/resources/BD_PARA_PRUEBAS.csv";
		File file = new File(csvFile);
		Reader reader = Files.newBufferedReader(file.toPath());

		CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(false).build();

		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(parser).build();

		String[] line;

		SportSchoolDto sportSchool = sportSchoolService.findByName("Santa Ana");

		List<AthleteDto> athletes = new ArrayList<>();
		while ((line = csvReader.readNext()) != null) {

			boolean authImg = line[0].trim().equals("SI") ? true : false;
			long familyCode = Long.valueOf(line[1].trim());
			long athleteCode = Long.valueOf(line[2].trim());
			List<String> split = Arrays.asList(line[4].split(" "));
			String firstSurname = split.get(0).trim();
			String secondSurname = split.subList(1, split.size()).stream().reduce((identity, accumulator) -> {
				return identity.trim() + " " + accumulator.trim();
			}).get();
			String name = line[5].trim();
			Date birthDate = dateFormat.parse(line[6].trim());
			String gender = line[9].trim().equals("Hombre") ? "male" : "female";
			String dni = line[10].trim();
			String category = line[11].trim();
			String licence = line[12].trim();
			String licenceType = line[13].trim().equals("T") ? LicenseType.Territorial.name()
					: LicenseType.National.name();
			String dorsalCategory = line[14].trim();
			String dorsalStr = line[15].trim();
			long dorsalNumber = 0L;
			if (!dorsalStr.isEmpty()) {
				dorsalNumber = Long.valueOf(dorsalStr);
			}
			String phone1 = line[16].trim();
			String phone2 = line[17].trim();
			String phone3 = line[18].trim();

			String address = line[19].trim();
			String postalCode = line[20].trim();
			String municipality = line[21].trim();

			String fatherMail = line[22].trim();
			String motherMail = line[23].trim();

			String feeType = line[31].trim();
			String iban = line[29].trim();
			int numDays = Integer.parseInt(line[34].trim().isEmpty() ? "0" : line[34].trim().substring(0, 1));

			// New Family
			FamilyDto family = familyService.findByCode(familyCode);
			if (family == null) {
				family = new FamilyDto();
				family.setCode(familyCode);
			}
			family.setFirstSurname(firstSurname);
			family.setSecondSurname(secondSurname);
			family.setFatherMail(fatherMail);
			family.setMotherMail(motherMail);
			family = familyService.save(family);

			// New Athlete
			AthleteDto athlete = athleteService.findByCode(athleteCode);
			if (athlete == null) {
				athlete = new AthleteDto();
				athlete.setCode(athleteCode);
			}
			athlete.setImageAuth(authImg);
			athlete.setName(name);
			athlete.setBirthDate(birthDate);
			athlete.setGender(gender);
			athlete.setDni(dni);
			athlete.setLicense(licence);
			athlete.setLicenseType(licenceType);
			athlete.setCategory(category);
			athlete.setDorsalNumber(dorsalNumber);
			athlete.setDorsalCategory(dorsalCategory);
			athlete.setPhone1(phone1);
			athlete.setPhone2(phone2);
			athlete.setPhone3(phone3);
			athlete.setAddress(address);
			athlete.setPostalCode(postalCode);
			athlete.setMunicipality(municipality);
			athlete.setFamilyId(family.getId());
			athlete.setName(name);
			athlete.setFeeType(feeType);
			athlete.setIban(iban);
			athlete.setNumDays(numDays);

			// Sport School
			athlete.setSportSchoolId(sportSchool.getId());

			final AthleteDto athleteFinal = athleteService.save(athlete);
			athletes.add(athleteFinal);

			// Group
			String groupName = line[32];
			GroupDto group = groupService.findByName(groupName);

			group.getScheduleIds().stream().forEach(schId -> {

				AthleteGroupScheduleDto agsDto = new AthleteGroupScheduleDto();
				agsDto.setAthleteId(athleteFinal.getId());
				agsDto.setGroupId(group.getId());
				agsDto.setScheduleId(schId);

				athleteGroupScheduleService.save(agsDto);
			});
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

		List<String> ssNames = Arrays.asList(new String[] { "Santa Ana", "Paracuellos" });

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
