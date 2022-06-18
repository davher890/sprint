package com.backend.sprint.service;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.GroupDao;
import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.GroupDto;
import com.backend.sprint.model.dto.ScheduleDto;
import com.backend.sprint.repository.GroupRepository;
import com.backend.sprint.repository.ScheduleRepository;
import com.backend.sprint.repository.SportSchoolRepository;
import com.backend.sprint.repository.TrainerRepository;
import com.backend.sprint.repository.UserRepository;
import com.backend.sprint.utils.ExcelUtils;

@Service
@Transactional
public class GroupService {

	@Autowired
	private GroupRepository repository;

	@Autowired
	private TrainerRepository trainerRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	private final Map<DayOfWeek, String> mapDay = new HashMap<DayOfWeek, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(DayOfWeek.MONDAY, "L");
			put(DayOfWeek.TUESDAY, "M");
			put(DayOfWeek.WEDNESDAY, "X");
			put(DayOfWeek.THURSDAY, "J");
			put(DayOfWeek.FRIDAY, "V");
			put(DayOfWeek.SATURDAY, "S");
			put(DayOfWeek.SUNDAY, "D");
		}
	};

	public Page<GroupDto> findPagintation(Specification<GroupDao> specification, Pageable pageable, String sort,
			Direction sortDirection) {
		Page<GroupDao> daoPage = repository.findAll(specification,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortDirection, sort)));
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<GroupDto> findAll() {
		return StreamSupport.stream(repository.findAll(Sort.by(Order.by("name")).ascending()).spliterator(), false)
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<GroupDto> findActive() {
		return StreamSupport.stream(repository.findActive().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public GroupDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public Set<GroupDto> findByTrainerId(long trainerId) {
		return repository.findByTrainerId(trainerId).stream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public List<GroupDto> findBySportSchoolIdAndGroupId(long sportSchoolId, Set<Long> groupIds,
			Boolean specialization) {

		return repository.findBySportSchoolIdAndGroupIds(sportSchoolId, groupIds, specialization).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<GroupDto> findBySportSchoolId(long sportSchoolId, Boolean specialization) {
		if (specialization == null) {
			return Stream
					.concat(repository.findBySportSchoolId(sportSchoolId, true).stream(),
							repository.findBySportSchoolId(sportSchoolId, false).stream())
					.map(this::convertToDto).sorted(Comparator.comparing(GroupDto::getName))
					.collect(Collectors.toList());
		}
		return repository.findBySportSchoolId(sportSchoolId, specialization).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public GroupDto findByNameAndSportSchool(String name, int sportSchoolId) {
		return convertToDto(repository.findByName(name, sportSchoolId));
	}

	public List<GroupDto> findBySpecialization(boolean specialization) {
		return repository.findBySpecialization(specialization).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public GroupDto save(GroupDto dto) {
		return convertToDto(repository.save(convertToDao(dto)));
	}

	public GroupDto convertToDto(GroupDao dao) {
		if (dao == null) {
			return null;
		}
		GroupDto dto = new ModelMapper().map(dao, GroupDto.class);
		if (dao.getTrainer() != null) {
			dto.setTrainerId(dao.getTrainer().getId());
		}
		if (dao.getSportSchool() != null) {
			dto.setSportSchoolId(dao.getSportSchool().getId());
			dto.setSportSchoolName(dao.getSportSchool().getName());
		}

		if (dao.getSchedules() != null) {
			Set<ScheduleDto> schedules = scheduleService.convertToDto(dao.getSchedules());
			dto.setScheduleIds(schedules.parallelStream().map(ScheduleDto::getId).collect(Collectors.toSet()));
			dto.setSchedules(schedules);
		}
		if (dao.getUpdatedBy() != null) {
			dto.setUpdatedById(dao.getUpdatedBy().getId());
		}

		return dto;
	}

	public GroupDao convertToDao(GroupDto dto) {
		if (dto == null) {
			return null;
		}

		GroupDao dao = new ModelMapper().map(dto, GroupDao.class);
		if (dto.getScheduleIds() != null) {
			dao.setSchedules(dto.getScheduleIds().stream().map(scheduleId -> {
				return scheduleRepository.findById(scheduleId).get();
			}).collect(Collectors.toSet()));
		}
		if (dto.getTrainerId() != 0) {
			dao.setTrainer(trainerRepository.findById(dto.getTrainerId()).get());
		}
		if (dto.getSportSchoolId() != 0) {
			dao.setSportSchool(sportSchoolRepository.findById(dto.getSportSchoolId()).get());
		}
		if (dto.getUpdatedById() != 0) {
			dao.setUpdatedBy(userRepository.findById(dto.getUpdatedById()).get());
		}

		return dao;
	}

	public List<ScheduleDto> findGroupSchedules(long id) {
		return repository.findById(id).get().getSchedules().stream().map(ScheduleDao::getId)
				.map(scheduleService::findById).collect(Collectors.toList());
	}

	public XSSFWorkbook getGroupAttendance(List<Long> ids, int month) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);

		LocalDate startDate;
		LocalDate endDate;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentMonth = cal.get(Calendar.MONTH);
		ZoneId zid = cal.getTimeZone() == null ? ZoneId.systemDefault() : cal.getTimeZone().toZoneId();

		if (currentMonth != month) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, month);
			startDate = LocalDateTime.ofInstant(cal.toInstant(), zid).toLocalDate();
			endDate = startDate.plusMonths(1);
		} else {
			startDate = LocalDate.now();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, month + 1);
			endDate = LocalDateTime.ofInstant(cal.toInstant(), zid).toLocalDate();
		}

		ids.stream().forEach(id -> {
			GroupDto group = this.findById(id);
			List<String> scheduleDays = group.getScheduleIds().parallelStream()
					.map(d -> scheduleService.findById(d).getDay()).collect(Collectors.toList());

			List<ExcelDataDto> data = new ArrayList<>();

			ExcelDataDto groupHeader = new ExcelDataDto();
			groupHeader.getData().add(new ExcelValueDto(group.getName(), CellType.STRING, style));
			groupHeader.getData().add(new ExcelValueDto(
					new DateFormatSymbols(new Locale("es", "ES")).getMonths()[month], CellType.STRING, style));
			data.add(groupHeader);

			ExcelDataDto header = new ExcelDataDto();
			header.getData().add(new ExcelValueDto("ATLETA", CellType.STRING, style));
			header.getData().add(new ExcelValueDto("FECHA NACIMIENTO", CellType.STRING, style));
			header.getData().add(new ExcelValueDto("CATEGORIA " + Calendar.getInstance().get(Calendar.YEAR),
					CellType.STRING, style));
			header.getData().add(new ExcelValueDto("DIAS", CellType.STRING, style));

			int nd = 0;
			for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

				DayOfWeek weekDay = date.getDayOfWeek();
				int monthDay = date.getDayOfMonth();

				if (scheduleDays.contains(weekDay.name())) {
					header.getData()
							.add(new ExcelValueDto(mapDay.get(weekDay) + "" + monthDay, CellType.STRING, style));
					nd++;
				}

			}
			data.add(header);
			final int numDays = nd;

			List<AthleteDao> athletes = athleteService.findDaoByGroupId(id);
			List<ExcelDataDto> excelData = athletes.parallelStream().map(entity -> {
				ExcelDataDto dataDto = new ExcelDataDto();
				dataDto.getData()
						.add(new ExcelValueDto(
								entity.getFirstSurname() + " " + entity.getSecondSurname() + ", " + entity.getName(),
								CellType.STRING, style));
				dataDto.getData()
						.add(new ExcelValueDto(dateFormat.format(entity.getBirthDate()), CellType.STRING, style));
				dataDto.getData().add(new ExcelValueDto(entity.getCategory(), CellType.STRING, style));
				dataDto.getData()
						.add(new ExcelValueDto(entity.getSchedules().parallelStream()
								.map(scheduleDao -> scheduleService.translateDay(scheduleDao.getDay()))
								.collect(Collectors.joining(",")), CellType.STRING, style));

				dataDto.getData().addAll(IntStream.range(0, numDays).parallel().mapToObj(i -> {
					return new ExcelValueDto("", CellType.STRING, style);
				}).collect(Collectors.toList()));
				return dataDto;
			}).collect(Collectors.toList());
			data.addAll(excelData);

			Sheet sheet = ExcelUtils.generateExcel(workbook, group.getName() + "_id_" + group.getId(), data);
			sheet.createFreezePane(0, 2);
			sheet.setMargin(Sheet.TopMargin, 0);
			sheet.setMargin(Sheet.BottomMargin, 0);
			sheet.setMargin(Sheet.LeftMargin, 0);
			sheet.setMargin(Sheet.RightMargin, 0);

			IntStream.range(0, 4 + numDays).parallel().forEach(i -> {
				sheet.autoSizeColumn(i);
			});
		});

		return workbook;
	}

	public GroupDto deleteById(long id) {

		GroupDto group = this.findById(id);
		List<AthleteDto> groupAthletes = athleteService.findByGroupId(id);
		if (groupAthletes != null && groupAthletes.size() > 0) {
			// Error: First remove athletes from group
			group.setErrorMessage("No se puede borrar un grupo que contiene atletas.");
			return group;
		} else {
			group.setActive(false);
			return this.save(group);
		}
	}

	public List<AthleteDto> findGroupAthletes(int id) {
		return athleteService.findByGroupId(id);
	}

	public List<GroupDto> swapGropupsAthletes(long groupAId, long groupBId, List<Long> groupAAthletes,
			List<Long> groupBAthletes) {

		for (Long athleteId : groupAAthletes) {
			AthleteDto athlete = athleteService.findById(athleteId);
			if (athlete.getGroupId() != groupAId) {
				athlete.setGroupId(groupAId);
				athleteService.save(athlete);
			}
		}

		for (Long athleteId : groupBAthletes) {
			AthleteDto athlete = athleteService.findById(athleteId);
			if (athlete.getGroupId() != groupBId) {
				athlete.setGroupId(groupBId);
				athleteService.save(athlete);
			}
		}

		return Arrays.asList(new GroupDto[] { this.findById(groupAId), this.findById(groupBId) });
	}

	public GroupDto swapGropupsAthletes(long groupId, List<Long> athletesIds) {

		for (Long athleteId : athletesIds) {
			AthleteDto athlete = athleteService.findById(athleteId);
			if (athlete.getGroupId() != groupId) {
				athlete.setGroupId(groupId);
				athleteService.save(athlete);
			}
		}

		return this.findById(groupId);
	}

}
