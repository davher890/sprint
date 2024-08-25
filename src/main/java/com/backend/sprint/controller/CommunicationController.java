
package com.backend.sprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.service.ExcelService;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.RegisteredAthleteService;
import com.backend.sprint.service.ScheduleService;

@RestController
@RequestMapping("communications")
public class CommunicationController {

	@Autowired
	private GroupService service;

	@Autowired
	private RegisteredAthleteService registeredAthleteService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private ExcelService excelService;

//	@PostMapping("")
//	public GroupDto save(@RequestBody GroupDto dto) {
//		return service.save(dto);
//	}
//
//	@GetMapping("/{id}/attendance")
//	public void getGroupAttendance(@PathVariable("id") long id,
//			@RequestParam(value = "month", required = true) int month,
//			@RequestParam(value = "sport_school_id", required = true) long sportSchoolId, HttpServletResponse response)
//			throws IOException {
//
//		List<Long> groupIds = new ArrayList<>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		UserDto user = (UserDto) auth.getPrincipal();
//
//		if (id == 0) {
//			if (user.getGroupIds() != null && user.getGroupIds().size() > 0) {
//				groupIds.addAll(user.getGroupIds());
//			} else {
//				List<GroupDto> findAll = service.findBySportSchoolId(sportSchoolId);
//				groupIds = findAll.parallelStream().filter(d -> d.getName() != null && !d.getName().isEmpty())
//						.map(GroupDto::getId).collect(Collectors.toList());
//			}
//		} else {
//			groupIds.add(id);
//		}
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		XSSFWorkbook workbook = service.getGroupAttendance(groupIds, month);
//		workbook.write(outputStream);
//		workbook.close();
//
//		IOUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), response.getOutputStream());
//	}
//
//	@GetMapping("/{idA}/swap/{idB}")
//	public List<GroupDto> swapGroupsAthletes(@PathVariable("idA") long groupAId, @PathVariable("idB") long groupBId,
//			@RequestParam(value = "groupAAthletes", required = true) List<Long> groupAAthletes,
//			@RequestParam(value = "groupBAthletes", required = true) List<Long> groupBAthletes) {
//		return service.swapGropupsAthletes(groupAId, groupBId, groupAAthletes, groupBAthletes);
//	}
//
//	@GetMapping("/{id}/swap")
//	public GroupDto swapGroupsAthletes(@PathVariable("id") long groupId,
//			@RequestParam(value = "athletesIds", required = true) List<Long> ahtletesIds) {
//		return service.swapGropupsAthletes(groupId, ahtletesIds);
//	}
}
