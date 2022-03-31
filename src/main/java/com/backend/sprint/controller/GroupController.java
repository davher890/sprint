
package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.specifications.AthleteSpecificationConstructor;
import com.backend.sprint.specifications.GroupSpecificationConstructor;

@RestController
@RequestMapping("groups")
public class GroupController {

	@Autowired
	private GroupService service;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private ExcelService excelService;

	@GetMapping("")
	public Page<GroupDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
			Pageable pageable) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("active__==__true");
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
	public GroupDto findById(@PathVariable long id) {
		return service.findById(id);
	}

	@DeleteMapping("/{id}")
	public GroupDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@GetMapping("/{id}/schedules")
	public List<ScheduleDto> findGroupSchedules(@PathVariable int id) {
		return service.findGroupSchedules(id);
	}

	@GetMapping("/{id}/athletes")
	public List<AthleteDto> findGroupAthletes(@PathVariable int id) {
		return service.findGroupAthletes(id);
	}

	@GetMapping("/{group_id}/schedules/{schedule_ids}/athletes")
	public Page<AthleteDto> findGroupScheduleAthletes(
			@RequestParam(value = "filters", required = false) List<String> filters, Pageable pageable,
			@PathVariable("group_id") int groupId, @PathVariable("schedule_ids") String scheduleIds) {
		if (filters == null) {
			filters = new ArrayList<String>();
		}
		filters.add("groupId__=__" + groupId);
		filters.add("scheduleIds__=__" + scheduleIds);
		return athleteService.findPagintation(new AthleteSpecificationConstructor<>(filters), pageable);
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
		filters.add("scheduleIds__=__" + scheduleIds);
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		List<AthleteDao> athletes = athleteService
				.findDaoPagintation(new AthleteSpecificationConstructor<>(filters), pageable).getContent();

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		excelService.downloadAthletesDaoExcel(columns, response, athletes, "Atletes");

	}

	@PostMapping("")
	public GroupDto save(@RequestBody GroupDto dto) {
		return service.save(dto);
	}

	@GetMapping("/{id}/attendance")
	public void getGroupAttendance(@PathVariable("id") long id,
			@RequestParam(value = "month", required = false) Integer month, HttpServletResponse response)
			throws IOException {

		if (month == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			month = cal.get(Calendar.MONTH);
		}

		List<Long> groupIds = new ArrayList<>();
		if (id == 0) {
			List<GroupDto> findAll = service.findActive();
			groupIds = findAll.parallelStream().filter(d -> d.getName() != null && !d.getName().isEmpty())
					.map(GroupDto::getId).collect(Collectors.toList());
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
}
