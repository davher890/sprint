
package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.service.ScheduleService;
import com.backend.sprint.specifications.GroupSpecificationConstructor;
import com.backend.sprint.specifications.RegisteredAthleteSpecificationConstructor;

@RestController
@RequestMapping("groups")
public class GroupController {

	@Autowired
	private GroupService service;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private ExcelService excelService;

	@GetMapping("")
	public Page<GroupDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable, @RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		Set<Long> sportSchoolIds = user.getSportSchoolIds();
		if (sportSchoolIds.size() == 0) {
			return Page.empty();
		}
		Set<Long> groupIds = user.getGroupIds();
		if (groupIds.size() > 0) {
			filters.add("id__in__" + groupIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		}
		filters.add(
				"sportSchoolId__in__" + sportSchoolIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		return service.findPagintation(new GroupSpecificationConstructor<>(filters), pageable, sort, sortDirection);
	}

	@GetMapping("/{id}")
	public GroupDto findById(@PathVariable long id) {
		return service.findById(id);
	}

	@DeleteMapping("/{id}")
	public GroupDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@GetMapping("/{id}/schedules")
	public List<ScheduleDto> findGroupSchedules(@PathVariable int id) {
		return scheduleService.findByGroupId(id);
	}

	@GetMapping("/{id}/athletes")
	public List<AthleteDto> findGroupAthletes(@PathVariable int id) {
		return service.findGroupAthletes(id);
	}

	@GetMapping("/{group_id}/schedules/{schedule_ids}/athletes")
	public Page<AthleteDto> findGroupScheduleAthletes(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@RequestParam(defaultValue = "id") String sort,
			@RequestParam(defaultValue = "DESC") Direction sortDirection, @PathVariable("group_id") int groupId,
			@PathVariable("schedule_ids") String scheduleIds) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");
		filters.add("groupId__=__" + groupId);
		filters.add("scheduleIds__=__" + scheduleIds.replaceAll("_", ","));
		return registeredAthleteService.findPagintation(new RegisteredAthleteSpecificationConstructor<>(filters),
				pageable, sort, sortDirection);
	}

	@PostMapping("/{group_id}/schedules/{schedule_ids}/athletes/excel")
	public void excel(@PathVariable("group_id") int groupId, @PathVariable("schedule_ids") String scheduleIds,
			@RequestBody List<ColumnDto> columns,
			@RequestParam(value = "filters", required = false) List<String> filters, HttpServletResponse response)
			throws IOException {

		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("groupId__=__" + groupId);
		filters.add("scheduleIds__=__" + scheduleIds.replaceAll("_", ","));
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<RegisteredAthleteDao> athletes = registeredAthleteService
				.findDaoPagintation(new RegisteredAthleteSpecificationConstructor<>(filters), pageable).getContent();

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		ByteArrayOutputStream outputStream = excelService.downloadAthletesExcel(columns, athletes, "Atletes");
		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
	}

	@PostMapping("")
	public GroupDto save(@RequestBody GroupDto dto) {
		return service.save(dto);
	}

	@GetMapping("/{id}/attendance")
	public void getGroupAttendance(@PathVariable("id") long id,
			@RequestParam(value = "month", required = true) int month,
			@RequestParam(value = "sport_school_id", required = true) long sportSchoolId, HttpServletResponse response)
			throws IOException {

		List<Long> groupIds = new ArrayList<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();

		if (id == 0) {
			if (user.getGroupIds() != null && user.getGroupIds().size() > 0) {
				groupIds.addAll(user.getGroupIds());
			} else {
				List<GroupDto> findAll = service.findBySportSchoolId(sportSchoolId);
				groupIds = findAll.parallelStream().filter(d -> d.getName() != null && !d.getName().isEmpty())
						.map(GroupDto::getId).collect(Collectors.toList());
			}
		} else {
			groupIds.add(id);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		XSSFWorkbook workbook = service.getGroupAttendance(groupIds, month);
		workbook.write(outputStream);
		workbook.close();

		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
	}

	@GetMapping("/{idA}/swap/{idB}")
	public List<GroupDto> swapGroupsAthletes(@PathVariable("idA") long groupAId, @PathVariable("idB") long groupBId,
			@RequestParam(value = "groupAAthletes", required = true) List<Long> groupAAthletes,
			@RequestParam(value = "groupBAthletes", required = true) List<Long> groupBAthletes) {
		return service.swapGropupsAthletes(groupAId, groupBId, groupAAthletes, groupBAthletes);
	}

	@GetMapping("/{id}/swap")
	public GroupDto swapGroupsAthletes(@PathVariable("id") long groupId,
			@RequestParam(value = "athletesIds", required = true) List<Long> ahtletesIds) {
		return service.swapGropupsAthletes(groupId, ahtletesIds);
	}
}
