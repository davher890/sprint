package com.backend.sprint.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.GenericAthleteDao;
import com.backend.sprint.model.dao.ReceiptAthleteDao;
import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.ExcelUtils;

@Service
public class ExcelService {

	private String DATE_FORMAT_MASK = "dd/MM/yyyy";

	@Autowired
	private FeeService feeService;

	@Autowired
	private MailService mailService;

	private ExcelValueDto getExcelValueFromEntity(String dataField, GenericAthleteDao entity, ColumnDto d)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);

		if (dataField.equals("sportSchoolId")) {
			return new ExcelValueDto(entity.getSportSchoolName(), ExcelUtils.getCellType(d));
		} else if (dataField.equals("birthDate")) {
			return new ExcelValueDto(entity.getBirthDate() != null ? dateFormat.format(entity.getBirthDate()) : null,
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("lastRegisterDate")) {
			return new ExcelValueDto(
					entity.getLastRegisterDate() != null ? dateFormat.format(entity.getLastRegisterDate()) : null,
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("lastUnregisterDate")) {
			return new ExcelValueDto(
					entity.getLastUnregisterDate() != null ? dateFormat.format(entity.getLastUnregisterDate()) : null,
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("licenseType")) {
			boolean h = entity.getLicense() == null || entity.getLicense().isEmpty()
					|| entity.getLicense().trim().toUpperCase().equals("SIN LICENCIA");
			return new ExcelValueDto(h ? null : entity.getLicenseType(), ExcelUtils.getCellType(d));
		} else if (dataField.equals("category")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(entity.getBirthDate());
			return new ExcelValueDto(AthleteUtils.calculateCategory(calendar.get(Calendar.YEAR)).name(),
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("days")) {
			String translatedDays = "";
			String days = entity.getDays();
			if (days != null && !days.isEmpty()) {
				translatedDays = Arrays.asList(days.split(",")).stream().sorted(new AthleteUtils.WeekDayComparator())
						.map(day -> AthleteUtils.dayTranslate.get(day)).collect(Collectors.joining(","));
			}
			return new ExcelValueDto(translatedDays, ExcelUtils.getCellType(d));
		} else if (dataField.equals("gender")) {
			return new ExcelValueDto(entity.getGender().equals("male") ? "Hombre" : "Mujer", ExcelUtils.getCellType(d));
		} else {
			return new ExcelValueDto(BeanUtils.getProperty(entity, dataField), ExcelUtils.getCellType(d));
		}
	}

	private ExcelValueDto getExcelValueFromEntity(String dataField, AthleteDao entity, ColumnDto d)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);

		if (dataField.equals("familyCode")) {
			return new ExcelValueDto(String.valueOf(entity.getFamily().getCode()), ExcelUtils.getCellType(d));
		} else if (dataField.equals("sportSchoolId")) {
			return new ExcelValueDto(entity.getSportSchool().getName(), ExcelUtils.getCellType(d));
		} else if (dataField.equals("groupName")) {
			return new ExcelValueDto(entity.getGroup().getName(), ExcelUtils.getCellType(d));
		} else if (dataField.equals("birthDate")) {
			return new ExcelValueDto(entity.getBirthDate() != null ? dateFormat.format(entity.getBirthDate()) : null,
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("lastRegisterDate")) {
			Date lastRegisterDate = AthleteUtils.getLastRegisterDate(entity);
			return new ExcelValueDto(dateFormat.format(lastRegisterDate != null ? lastRegisterDate : null),
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("lastUnregisterDate")) {
			Date lastUnregisterDate = AthleteUtils.getLastUnregisterDate(entity);
			return new ExcelValueDto(lastUnregisterDate != null ? dateFormat.format(lastUnregisterDate) : null,
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("category")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(entity.getBirthDate());
			return new ExcelValueDto(AthleteUtils.calculateCategory(calendar.get(Calendar.YEAR)).name(),
					ExcelUtils.getCellType(d));
		} else if (dataField.equals("numDays")) {
			int numDays = 0;
			if (entity.getSchedules() != null && entity.getSchedules().size() > 0) {
				numDays = entity.getSchedules().size();
			}
			return new ExcelValueDto(String.valueOf(numDays), ExcelUtils.getCellType(d));
		} else if (dataField.equals("days")) {
			String translatedDays = "";
			String days = entity.getSchedules().stream().sorted(Comparator.comparingInt(ScheduleDao::getDayNumber))
					.map(sch -> sch.getDay()).collect(Collectors.joining(","));
			try {
				if (days != null && !days.isEmpty()) {
					translatedDays = Arrays.asList(days.split(",")).stream()
							.map(day -> AthleteUtils.dayTranslate.get(day)).collect(Collectors.joining(","));
				}
			} catch (Exception e) {
				System.out.print(entity.getId());
			}
			return new ExcelValueDto(translatedDays, ExcelUtils.getCellType(d));
		} else if (dataField.equals("gender")) {
			return new ExcelValueDto(entity.getGender().equals("male") ? "Hombre" : "Mujer", ExcelUtils.getCellType(d));
		} else {
			return new ExcelValueDto(BeanUtils.getProperty(entity, dataField), ExcelUtils.getCellType(d));
		}

	}

	public ByteArrayOutputStream downloadAthletesExcel(List<ColumnDto> columns,
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
					String dataField = d.getDataField();
					return getExcelValueFromEntity(dataField, entity, d);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		return ExcelUtils.generateExcel(excelName, data);

	}

	public ByteArrayOutputStream downloadAthletesDaoExcel(List<ColumnDto> columns, List<AthleteDao> athletes,
			String excelName) throws IOException {
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
					String dataField = d.getDataField();
					return getExcelValueFromEntity(dataField, entity, d);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		return ExcelUtils.generateExcel(excelName, data);
	}

	public ByteArrayOutputStream downloadReceiptAthletesExcel(List<ReceiptAthleteDao> receiptAthletes, String concept,
			String excelName) throws IOException {
		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().add(new ExcelValueDto("REFERENCIA", CellType.STRING));
		header.getData().add(new ExcelValueDto("NOMBRE", CellType.STRING));
		header.getData().add(new ExcelValueDto("IBAN", CellType.STRING));
		header.getData().add(new ExcelValueDto("BIC", CellType.STRING));
		header.getData().add(new ExcelValueDto("PAIS", CellType.STRING));
		header.getData().add(new ExcelValueDto("RESIDENTE", CellType.STRING));
		header.getData().add(new ExcelValueDto("REFMANDATO", CellType.STRING));
		header.getData().add(new ExcelValueDto("FECHAFIRMAMANDATO", CellType.STRING));
		header.getData().add(new ExcelValueDto("REFADEUDO", CellType.STRING));
		header.getData().add(new ExcelValueDto("IMPORTE", CellType.STRING));
		header.getData().add(new ExcelValueDto("CONCEPTO", CellType.STRING));
		header.getData().add(new ExcelValueDto("TIPOADEUDO", CellType.STRING));
		header.getData().add(new ExcelValueDto("FECHACOBRO", CellType.STRING));
		data.add(header);

		data.addAll(receiptAthletes.parallelStream().map(entity -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);
			ExcelDataDto dataDto = new ExcelDataDto();

			AthleteDao athlete = entity.getAthlete();

			dataDto.getData().add(new ExcelValueDto(String.valueOf(athlete.getCode()), CellType.NUMERIC));
			dataDto.getData()
					.add(new ExcelValueDto(
							athlete.getFirstSurname() + " " + athlete.getSecondSurname() + ", " + athlete.getName(),
							CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(athlete.getIban(), CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto("NOTPROVIDED", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("ES", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("S", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(String.valueOf(athlete.getCode()), CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto("31/10/2009", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(concept, CellType.STRING));
			dataDto.getData()
					.add(new ExcelValueDto(
							String.valueOf(
									entity.getMonthlyFee() + entity.getEnrollmentFee() + entity.getMembershipFee()),
							CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto(concept, CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("RCUR", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(dateFormat.format(new Date()), CellType.STRING));

			return dataDto;
		}).collect(Collectors.toList()));

		ByteArrayOutputStream outputStream = ExcelUtils.generateExcel(excelName, data);
		mailService.sendExcelMail(outputStream);
		return outputStream;
	}
}
