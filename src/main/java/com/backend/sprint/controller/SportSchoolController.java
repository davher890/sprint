package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.backend.sprint.model.dao.SportSchoolDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.SelectOptionDto;
import com.backend.sprint.model.dto.SportSchoolDto;
import com.backend.sprint.model.dto.TrainerDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.service.GroupService;
import com.backend.sprint.service.SportSchoolService;
import com.backend.sprint.service.TrainerService;
import com.backend.sprint.service.UserService;
import com.backend.sprint.specifications.SportSchoolSpecificationConstructor;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("sport_schools")
public class SportSchoolController {

	@Autowired
	private SportSchoolService service;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TrainerService trainerService;

	@Autowired
	private UserService userService;

	@GetMapping("")
	public Page<SportSchoolDto> findPagintation(@RequestParam(value = "filters", required = false) List<String> filters,
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
		filters.add("id__in__" + sportSchoolIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
		return service.findPagintation(new SportSchoolSpecificationConstructor<>(filters), pageable, sort,
				sortDirection);
	}

	@GetMapping("/all")
	public List<SportSchoolDto> findAll() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();

		if (user.isAdmin()) {
			return service.findAll();
		}
		return service.findAllById(user.getSportSchoolIds());
	}

	@GetMapping("/{id}")
	public SportSchoolDto findById(@PathVariable int id) {
		return service.findById(id);
	}

	@GetMapping("/{id}/fee_types")
	public List<SelectOptionDto> findFeeTypesById(@PathVariable int id) {
		return service.findFeeTypesById(id);
	}

	@GetMapping("/{id}/groups")
	public List<GroupDto> findSportSchoolGroups(@PathVariable int id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();

		if (!user.isAdmin()) {
			Set<Long> groupIds = user.getGroupIds();
			if (groupIds != null && groupIds.size() > 0) {
				return groupService.findBySportSchoolIdAndGroupId(id, groupIds);
			}
		}

		return groupService.findBySportSchoolId(id);
	}

	@GetMapping("/groups")
	public List<GroupDto> findSportSchoolsGroups(
			@RequestParam(value = "ids", required = false) List<Long> sportSchoolIds) {

		return groupService.findBySportSchoolIds(sportSchoolIds);
	}

	@GetMapping("/{id}/trainers")
	public Set<TrainerDto> findSportSchoolTrainers(@PathVariable int id) {
		return trainerService.findBySportSchoolId(id);
	}

	@PostMapping("")
	public SportSchoolDto save(@RequestBody SportSchoolDto ss) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
//		dto.setUpdatedById(user.getId());
//		Error when uncomment the previous line
		final SportSchoolDto dto = service.save(ss);

		userService.findAdminUsers().forEach(d -> {
			d.getSportSchoolIds().add(dto.getId());
			userService.save(d);
		});

		user.getSportSchoolIds().add(dto.getId());
		userService.save(user);

		return dto;
	}

	@DeleteMapping("/{id}")
	public SportSchoolDto deleteById(@PathVariable long id) {
		return service.deleteById(id);
	}

	@PostMapping("/excel")
	public void excel(@RequestBody List<ColumnDto> columns, HttpServletResponse response) throws IOException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();

		List<SportSchoolDao> sportSchools = service.findAllByIdDao(user.getSportSchoolIds());

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		data.addAll(sportSchools.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
				try {
					return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
							ExcelUtils.getCellType(d));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Escuelas", data);
		IOUtils.copy(bas, response.getOutputStream());
	}
}
