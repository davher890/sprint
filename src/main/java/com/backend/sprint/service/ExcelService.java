package com.backend.sprint.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.utils.ExcelUtils;

@Service
public class ExcelService {

	public void downloadAthletesExcel(List<ColumnDto> columns, HttpServletResponse response, List<AthleteDto> athletes,
			String excelName) throws IOException {
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
					if (d.getDataField().equals("sportSchoolId")) {
						return new ExcelValueDto(entity.getSportSchoolName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("groupName")) {
						return new ExcelValueDto(entity.getGroupName(), ExcelUtils.getCellType(d));
					}
//					else if (d.getDataField().equals("schedules")) {
//						return new ExcelValueDto(String.join(" , ", entity.getSchedules().parallelStream().map(s -> {
//							return s.getDay() + " " + s.getStartHour() + ":" + s.getStartMinute() + "-" + s.getEndHour()
//									+ ":" + s.getEndMinute();
//						}).collect(Collectors.toList())), ExcelUtils.getCellType(d));
//					}
					return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
							ExcelUtils.getCellType(d));
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

		data.addAll(athletes.parallelStream().map(entity -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			dataDto.getData().addAll(columns.parallelStream().filter(ColumnDto::isShow).map(d -> {
				try {
					if (d.getDataField().equals("sportSchoolId")) {
						return new ExcelValueDto(entity.getSportSchool().getName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("groupName")) {
						return new ExcelValueDto(entity.getGroup().getName(), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("schedules")) {
						return new ExcelValueDto(String.join(" , ", entity.getSchedules().parallelStream().map(s -> {
							return s.getDay() + " " + s.getStartHour() + ":" + s.getStartMinute() + "-" + s.getEndHour()
									+ ":" + s.getEndMinute();
						}).collect(Collectors.toList())), ExcelUtils.getCellType(d));
					}
					return new ExcelValueDto(BeanUtils.getProperty(entity, d.getDataField()),
							ExcelUtils.getCellType(d));
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
