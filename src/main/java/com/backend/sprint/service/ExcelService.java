package com.backend.sprint.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.GenericAthleteDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.ExcelUtils;

@Service
public class ExcelService {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	@Autowired
	private ScheduleService scheduleService;

	public void downloadAthletesExcel(List<ColumnDto> columns, HttpServletResponse response,
			List<? extends GenericAthleteDao> athletes, String excelName) throws IOException {
		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		data.addAll(athletes.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					if (d.getDataField().equals("sportSchoolId")) {
						return new ExcelValueDto(entity.getSportSchoolName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("birthDate")) {
						return new ExcelValueDto(
								entity.getBirthDate() != null ? dateFormat.format(entity.getBirthDate()) : null,
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("lastRegisterDate")) {
						return new ExcelValueDto(
								entity.getLastRegisterDate() != null ? dateFormat.format(entity.getLastRegisterDate())
										: null,
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("lastUnregisterDate")) {
						return new ExcelValueDto(entity.getLastUnregisterDate() != null
								? dateFormat.format(entity.getLastUnregisterDate())
								: null, ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("licenseType")) {
						boolean h = entity.getLicense() == null || entity.getLicense().isEmpty()
								|| entity.getLicense().trim().toUpperCase().equals("SIN LICENCIA");
						return new ExcelValueDto(h ? null : entity.getLicenseType(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("days")) {
						String translatedDays = "";
						String days = entity.getDays();
						if (days != null && !days.isEmpty()) {
							translatedDays = Arrays.asList(days.split(",")).stream()
									.map(day -> scheduleService.translateDay(day)).collect(Collectors.joining(","));
						}
						return new ExcelValueDto(translatedDays, ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("gender")) {
						return new ExcelValueDto(entity.getGender().equals("male") ? "Hombre" : "Mujer",
								ExcelUtils.getCellType(d));
					} else {
						return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
								ExcelUtils.getCellType(d));
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel(excelName, data);
		IOUtils.copy(bas, response.getOutputStream());
	}

	public void downloadAthletesDaoExcel(List<ColumnDto> columns, HttpServletResponse response,
			List<AthleteDao> athletes, String excelName) throws IOException {
		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
			return new ExcelValueDto(d.getText(), CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		data.addAll(athletes.stream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.stream().filter(ColumnDto::isShow).map(d -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					if (d.getDataField().equals("sportSchoolId")) {
						return new ExcelValueDto(entity.getSportSchool().getName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("groupName")) {
						return new ExcelValueDto(entity.getGroup().getName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("birthDate")) {
						return new ExcelValueDto(
								entity.getBirthDate() != null ? dateFormat.format(entity.getBirthDate()) : null,
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("lastRegisterDate")) {
						Date lastRegisterDate = AthleteUtils.getLastRegisterDate(entity);
						return new ExcelValueDto(dateFormat.format(lastRegisterDate != null ? lastRegisterDate : null),
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("lastUnregisterDate")) {
						Date lastUnregisterDate = AthleteUtils.getLastUnregisterDate(entity);
						return new ExcelValueDto(
								lastUnregisterDate != null ? dateFormat.format(lastUnregisterDate) : null,
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("numDays")) {
						int numDays = 0;
						if (entity.getSchedules() != null && entity.getSchedules().size() > 0) {
							numDays = entity.getSchedules().size();
						}
						return new ExcelValueDto(String.valueOf(numDays), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("days")) {
						String translatedDays = "";
						String days = entity.getSchedules().parallelStream().map(sch -> sch.getDay())
								.collect(Collectors.joining(","));
						if (days != null && !days.isEmpty()) {
							translatedDays = Arrays.asList(days.split(",")).stream()
									.map(day -> scheduleService.translateDay(day)).collect(Collectors.joining(","));
						}
						return new ExcelValueDto(translatedDays, ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("gender")) {
						return new ExcelValueDto(entity.getGender().equals("male") ? "Hombre" : "Mujer",
								ExcelUtils.getCellType(d));
					} else {
						return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
								ExcelUtils.getCellType(d));
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel(excelName, data);
		IOUtils.copy(bas, response.getOutputStream());
	}
}
