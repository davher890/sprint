
package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.AthleteGroupScheduleDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.service.AthleteGroupScheduleService;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.ScheduleService;
import com.backend.sprint.specifications.AthleteGroupScheduleSpecificationConstructor;
import com.backend.sprint.specifications.GroupSpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("groups")
public class GroupController {

	@Autowired
	private GroupService service;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@GetMapping("")
	public Page<GroupDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new GroupSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<GroupDto> findAll(@RequestParam(value = "specialization", required = false) Boolean specialization) {

		if (specialization == null) {
			return service.findAll();
		} else {
			return service.findBySpecialization(specialization);
		}
	}

	@GetMapping("/{id}")
	public GroupDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/schedules")
	public List<ScheduleDto> findGroupSchedules(@PathVariable int id) {
		return service.findGroupSchedules(id);
	}

	@GetMapping("/{group_id}/schedules/{schedule_id}/athletes")
	public Page<AthleteDto> findGroupScheduleAthletes(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@PathVariable("group_id") int groupId, @PathVariable("schedule_id") int scheduleId) {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("group_id__EQUALS__" + groupId);
		filters.add("schedule_id__EQUALS__" + scheduleId);

		return athleteService.findByGroupAndSchedule(new AthleteGroupScheduleSpecificationConstructor<>(filters),
				pageable);
	}

	@PostMapping("")
	public GroupDto save(@RequestBody GroupDto dto) {
		return service.save(dto);
	}

	@GetMapping("/{id}/attendance")
	public void getGroupAttendance(@PathVariable("id") int id, HttpServletResponse response) throws IOException {

		List<AthleteDto> athletes = athleteGroupScheduleService.findByGroup(id).parallelStream()
				.map(d -> athleteService.findById(d.getAthleteId())).collect(Collectors.toList());

		List<ExcelDataDto> data = athletes.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().add(new ExcelValueDto(entity.getName(), CellType.STRING));
			return dataDto;
		}).collect(Collectors.toList());

		GroupDto group = service.findById(id);
		List<String> scheduleDays = group.getScheduleIds().stream().map(d -> scheduleService.findById(d).getDay())
				.collect(Collectors.toList());

		List<String> headers = new ArrayList<String>();
		headers.add("Atleta");
		for (LocalDate date = LocalDate.now(); date.isBefore(LocalDate.now().plusMonths(1)); date = date.plusDays(1)) {

			String weekDay = date.getDayOfWeek().name();
			int monthDay = date.getDayOfMonth();

			if (scheduleDays.contains(weekDay)) {
				headers.add(weekDay + " - " + monthDay);
			}
		}
		ByteArrayInputStream bas = ExcelUtils.generateExcel("Familias", headers, data);
		IOUtils.copy(bas, response.getOutputStream());
	}
}
