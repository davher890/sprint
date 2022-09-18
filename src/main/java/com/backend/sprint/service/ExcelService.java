package com.backend.sprint.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dao.AthleteDao;
import com.backend.sprint.model.dao.GenericAthleteDao;
import com.backend.sprint.model.dao.RegisteredAthleteDao;
import com.backend.sprint.model.dao.ScheduleDao;
import com.backend.sprint.model.dto.ColumnDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.FeeDto;
import com.backend.sprint.model.dto.UserDto;
import com.backend.sprint.utils.AthleteUtils;
import com.backend.sprint.utils.ExcelUtils;

@Service
public class ExcelService {

	private String DATE_FORMAT_MASK = "dd/MM/yyyy";

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private FeeService feeService;

	public ByteArrayOutputStream downloadAthletesExcel(List<ColumnDto> columns, HttpServletResponse response,
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
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);
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
					} else if (d.getDataField().equals("category")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(entity.getBirthDate());
						return new ExcelValueDto(AthleteUtils.calculateCategory(calendar.get(Calendar.YEAR)).name(),
								ExcelUtils.getCellType(d));
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
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);
					if (d.getDataField().equals("familyCode")) {
						return new ExcelValueDto(String.valueOf(entity.getFamily().getCode()),
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("sportSchoolId")) {
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
					} else if (d.getDataField().equals("category")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(entity.getBirthDate());
						return new ExcelValueDto(AthleteUtils.calculateCategory(calendar.get(Calendar.YEAR)).name(),
								ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("numDays")) {
						int numDays = 0;
						if (entity.getSchedules() != null && entity.getSchedules().size() > 0) {
							numDays = entity.getSchedules().size();
						}
						return new ExcelValueDto(String.valueOf(numDays), ExcelUtils.getCellType(d));
					} else if (d.getDataField().equals("days")) {
						String translatedDays = "";
						String days = entity.getSchedules().stream()
								.sorted(Comparator.comparingInt(ScheduleDao::getDayNumber)).map(sch -> sch.getDay())
								.collect(Collectors.joining(","));
						try {
							if (days != null && !days.isEmpty()) {
								translatedDays = Arrays.asList(days.split(",")).stream()
										.map(day -> scheduleService.translateDay(day)).collect(Collectors.joining(","));
							}
						} catch (Exception e) {
							System.out.print(entity.getId());
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

		return ExcelUtils.generateExcel(excelName, data);
	}

	public ByteArrayOutputStream downloadPaymentAthletesExcel(List<ColumnDto> columns,
			List<? extends GenericAthleteDao> athletes, String concept, String excelName) throws IOException {
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

		data.addAll(((List<RegisteredAthleteDao>) athletes).parallelStream().map(entity -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK);
			ExcelDataDto dataDto = new ExcelDataDto();

			Set<Long> scheduleIds = new HashSet<Long>();
			if (entity.getScheduleIds() != null && !entity.getScheduleIds().isEmpty()) {
				String[] split = entity.getScheduleIds().split(",");
				List<Long> asList = Arrays.asList(split).stream().map(d -> Long.valueOf(d))
						.collect(Collectors.toList());
				scheduleIds = new HashSet<Long>(asList);
			}
			FeeDto fee = feeService.getAthleteFee(entity.getId(), entity.getFamilyCode(), entity.getPaymentType(),
					entity.getSportSchoolId(), entity.isSpecialization(), scheduleIds, entity.getCategory(),
					entity.getLicenseType(), entity.isEnrollmentFeePaid());

			dataDto.getData().add(new ExcelValueDto(String.valueOf(entity.getCode()), CellType.NUMERIC));
			dataDto.getData()
					.add(new ExcelValueDto(
							entity.getFirstSurname() + " " + entity.getSecondSurname() + ", " + entity.getName(),
							CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(entity.getIban(), CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto("NOTPROVIDED", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("ES", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("S", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(String.valueOf(entity.getCode()), CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto("31/10/2009", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(concept, CellType.STRING));
			dataDto.getData()
					.add(new ExcelValueDto(
							String.valueOf(fee.getMonthlyFee() + fee.getEnrollmentFee() + fee.getMembershipFee()),
							CellType.NUMERIC));
			dataDto.getData().add(new ExcelValueDto(concept, CellType.STRING));
			dataDto.getData().add(new ExcelValueDto("RCUR", CellType.STRING));
			dataDto.getData().add(new ExcelValueDto(dateFormat.format(new Date()), CellType.STRING));

			return dataDto;
		}).collect(Collectors.toList()));

		ByteArrayOutputStream outputStream = ExcelUtils.generateExcel(excelName, data);
		sendExcelMail(outputStream);
		return outputStream;
	}

	private void sendExcelMail(ByteArrayOutputStream outputStream) {
		// Recipient's email ID needs to be mentioned.
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		String to = user.getEmail();

		// Sender's email ID needs to be mentioned
		String from = "davher890@gmail.com";

		// Assuming you are sending email from through gmails smtp
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("davher890@gmail.com", "REDACTED");
			}
		});

		// Used to debug SMTP issues
		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("Descarga de datos");

			// Now set the actual message
			message.setText("Adjunto se encuentra el excel con la información solicitada");

			Multipart multipart = new MimeMultipart();
			MimeBodyPart attachmentPart = new MimeBodyPart();
			MimeBodyPart textPart = new MimeBodyPart();
			try {
				File f = new File("/tmp/excel.xlsx");
				FileOutputStream fout = new FileOutputStream(f);

				attachmentPart.attachFile(f);
				textPart.setText("Adjunto se encuentra el excel con la información solicitada");
				multipart.addBodyPart(textPart);
				multipart.addBodyPart(attachmentPart);
			} catch (IOException e) {
				e.printStackTrace();
			}

			message.setContent(multipart);
			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
