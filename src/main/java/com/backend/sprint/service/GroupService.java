package com.backend.sprint.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private ScheduleService scheduleService;

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private AthleteGroupScheduleService athleteGroupScheduleService;

	@Autowired
	private SportSchoolRepository sportSchoolRepository;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	public Page<GroupDto> findPagintation(Specification<GroupDao> specification, Pageable pageable) {
		Page<GroupDao> daoPage = repository.findAll(specification, pageable);
		return daoPage.map(dao -> convertToDto(dao));
	}

	public List<GroupDto> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public GroupDto findById(long id) {
		return convertToDto(repository.findById(id).orElse(null));
	}

	public Set<GroupDto> findByTrainerId(long idtrainer) {
		return repository.findByTrainerId(idtrainer).stream().map(this::convertToDto).collect(Collectors.toSet());
	}

	public GroupDto findByName(String name) {
		return convertToDto(repository.findByName(name));
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
		}
		if (dao.getSchedules() != null) {
			dto.setScheduleIds(dao.getSchedules().stream().map(ScheduleDao::getId).collect(Collectors.toSet()));
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
		return dao;
	}

	public List<ScheduleDto> findGroupSchedules(long id) {
		return repository.findById(id).get().getSchedules().stream().map(ScheduleDao::getId)
				.map(scheduleService::findById).collect(Collectors.toList());
	}

	public XSSFWorkbook getGroupAttendance(List<Long> ids) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();

		ids.stream().forEach(id -> {
			List<AthleteDto> athletes = athleteGroupScheduleService.findByGroup(id).parallelStream()
					.map(d -> d.getAthleteId()).distinct().map(d -> athleteService.findById(d))
					.collect(Collectors.toList());
			GroupDto group = this.findById(id);
			List<String> scheduleDays = group.getScheduleIds().stream().map(d -> scheduleService.findById(d).getDay())
					.collect(Collectors.toList());

			List<ExcelDataDto> data = new ArrayList<>();

			ExcelDataDto groupHeader = new ExcelDataDto();
			groupHeader.getData().add(new ExcelValueDto(group.getName(), CellType.STRING));
			data.add(groupHeader);

			ExcelDataDto header = new ExcelDataDto();
			header.getData().add(new ExcelValueDto("ATLETA", CellType.STRING));
			header.getData().add(new ExcelValueDto("FECHA NACIMIENTO", CellType.STRING));
			header.getData()
					.add(new ExcelValueDto("CATEGORIA " + Calendar.getInstance().get(Calendar.YEAR), CellType.STRING));
			header.getData().add(new ExcelValueDto("DIAS", CellType.STRING));

			for (LocalDate date = LocalDate.now(); date
					.isBefore(LocalDate.now().plusMonths(1)); date = date.plusDays(1)) {

				String weekDay = date.getDayOfWeek().name();
				int monthDay = date.getDayOfMonth();

				if (scheduleDays.contains(weekDay)) {
					header.getData().add(new ExcelValueDto(weekDay + " - " + monthDay, CellType.STRING));
				}
			}
			data.add(header);

			List<ExcelDataDto> excelData = athletes.parallelStream().map(entity -> {
				ExcelDataDto dataDto = new ExcelDataDto();
				dataDto.getData()
						.add(new ExcelValueDto(
								entity.getSecondSurname() + " " + entity.getFirstSurname() + ", " + entity.getName(),
								CellType.STRING));
				dataDto.getData().add(new ExcelValueDto(dateFormat.format(entity.getBirthDate()), CellType.STRING));
				dataDto.getData().add(new ExcelValueDto(entity.getCategory(), CellType.STRING));
				dataDto.getData().add(new ExcelValueDto(entity.getSchedules().parallelStream()
						.map(ScheduleDto::getDayTranslate).collect(Collectors.joining(",")), CellType.STRING));
				return dataDto;
			}).collect(Collectors.toList());
			data.addAll(excelData);
			ExcelUtils.generateExcel(workbook, group.getName(), data);
		});

		return workbook;

	}

}
