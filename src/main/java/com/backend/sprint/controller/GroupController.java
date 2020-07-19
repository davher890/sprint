package com.backend.sprint.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.specifications.AthleteGroupScheduleSpecificationConstructor;
import com.backend.sprint.specifications.GroupSpecificationConstructor;

@RestController
@RequestMapping("groups")
public class GroupController {

	@Autowired
	private GroupService service;

	@Autowired
	private AthleteService athleteService;

	@GetMapping("")
	public Page<GroupDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		return service.findPagintation(new GroupSpecificationConstructor<>(filters), pageable);
	}

	@GetMapping("/all")
	public List<GroupDto> findAll() {
		return service.findAll();
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
}
