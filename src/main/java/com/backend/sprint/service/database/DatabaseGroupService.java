package com.backend.sprint.service.database;

import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.ScheduleService;
import com.backend.sprint.service.SportSchoolService;
import com.backend.sprint.service.TrainerService;
import com.opencsv.CSVReader;

@Service
public class DatabaseGroupService extends DatabaseService {

	private static final int GROUP_NAME = 0;
	private static final int WEEK_DAYS = 1;
	private static final int SCHEDULE = 2;

	private static List<String> specializationGroupNames = Arrays
			.asList(new String[] { "ALVARO-FONDO M-J", "ALVARO-VELOCIDAD L-X", "DAVID-LANZAMIENTOS", "JAVI-FONDO",
					"JAVI-VELOCIDAD", "JESUS-COMBINADAS", "JOSE-SALTOS" });
	@Autowired
	private TrainerService trainerService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SportSchoolService sportSchoolService;

	public List<GroupDto> dbFillGroupsData(String fileName) throws IOException, ParseException {

		CSVReader csvReader = readFile(fileName);

		return StreamSupport.stream(csvReader.spliterator(), true).map(line -> {
			// Group
			String groupName = line[GROUP_NAME];
			String weekDay = line[WEEK_DAYS];
			String schedule = line[SCHEDULE];

			if (!groupName.isEmpty() && !weekDay.isEmpty() && !schedule.isEmpty()) {
				return createGroup(groupName, weekDay, schedule);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
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
		String option3 = "M -J 19:00-20:30 y V 17:30-19:00";
		String option4 = "L -X 19:00-20:30 y V 17:30-19:00";
		String option5 = "M -J 19:00-20:30 y V 18:00-19:30";
		String option6 = "M-J 18:00 - 19:30 y V 17:30-19:00";

		String scheduleTrim = schedule.trim();
		if (scheduleTrim.equals(option1.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.MONDAY.name(), 17, 45, 19, 15).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.WEDNESDAY.name(), 17, 45, 19, 15).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.THURSDAY.name(), 19, 15, 20, 45).getId());
		} else if (scheduleTrim.equals(option2.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.MONDAY.name(), 18, 0, 19, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.TUESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.THURSDAY.name(), 19, 0, 20, 30).getId());
		} else if (scheduleTrim.equals(option3.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.TUESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.THURSDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.FRIDAY.name(), 17, 30, 10, 0).getId());
		} else if (scheduleTrim.equals(option4.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.MONDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.WEDNESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.FRIDAY.name(), 17, 30, 10, 0).getId());
		} else if (scheduleTrim.equals(option5.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.TUESDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.THURSDAY.name(), 19, 0, 20, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.FRIDAY.name(), 18, 0, 19, 30).getId());
		} else if (scheduleTrim.equals(option6.trim())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.TUESDAY.name(), 18, 0, 19, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.THURSDAY.name(), 18, 0, 19, 30).getId());
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.FRIDAY.name(), 17, 30, 19, 0).getId());
		} else {
			List<String> days = Arrays.asList(weekDay.replace("y", ",").split(","));
			group.getScheduleIds().addAll(days.stream().map(dayStr -> {
				int startHour = Integer.parseInt(scheduleTrim.split("-")[0].split(":")[0].trim());
				int startMinute = Integer.parseInt(scheduleTrim.split("-")[0].split(":")[1].trim());

				int endHour = Integer.parseInt(scheduleTrim.split("-")[1].split(":")[0].trim());
				int endMinute = Integer.parseInt(scheduleTrim.split("-")[1].split(":")[1].trim());

				ScheduleDto id = findOrCreateSchedule(translateWeekDay(dayStr).name(), startHour, startMinute, endHour,
						endMinute);
				return id.getId();
			}).collect(Collectors.toSet()));
		}
		if (specializationGroupNames.contains(group.getName())) {
			group.getScheduleIds().add(findOrCreateSchedule(DayOfWeek.FRIDAY.name(), 17, 30, 19, 0).getId());
		}
		return groupService.save(group);
	}

	private ScheduleDto findOrCreateSchedule(String day, int startHour, int startMinute, int endHour, int endMinute) {

		ScheduleDto schedule = scheduleService.findByTime(day, startHour, startMinute, endHour, endMinute);

		if (schedule == null) {
			schedule = new ScheduleDto();
			schedule.setDay(day);
			schedule.setStartHour(startHour);
			schedule.setEndHour(endHour);
			schedule.setStartMinute(startMinute);
			schedule.setEndMinute(endMinute);
			schedule = scheduleService.save(schedule);
		}
		return schedule;
	}

	private DayOfWeek translateWeekDay(String day) {

		if (day.trim().toLowerCase().equals("lunes")) {
			return DayOfWeek.MONDAY;
		}
		if (day.trim().toLowerCase().equals("martes")) {
			return DayOfWeek.TUESDAY;
		}
		if (day.trim().toLowerCase().equals("miercoles")) {
			return DayOfWeek.WEDNESDAY;
		}
		if (day.trim().toLowerCase().equals("jueves")) {
			return DayOfWeek.THURSDAY;
		}
		if (day.trim().toLowerCase().equals("viernes")) {
			return DayOfWeek.FRIDAY;
		} else {
			return null;
		}
	}

}
