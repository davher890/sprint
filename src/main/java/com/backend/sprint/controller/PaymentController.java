
package com.backend.sprint.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.sprint.model.dto.AthleteDto;
import com.backend.sprint.model.dto.ExcelDataDto;
import com.backend.sprint.model.dto.ExcelValueDto;
import com.backend.sprint.model.dto.PaymentQueryDto;
import com.backend.sprint.service.AthleteService;
import com.backend.sprint.service.PaymentService;
import com.backend.sprint.utils.ExcelUtils;

@RestController
@RequestMapping("payments")
public class PaymentController {

	private static List<String> columns = Arrays.asList("REFERENCIA", "NOMBRE", "IBAN", "BIC", "PAIS", "RESIDENTE",
			"REFMANDATO", "FECHAFIRMAMANDATO", "REFADEUDO", "IMPORTE", "CONCEPTO", "TIPOADEUDO", "FECHACOBRO");

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private AthleteService athleteService;

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/payment_list")
	public void save(@RequestBody PaymentQueryDto query, HttpServletResponse response)
			throws ParseException, IOException {

		List<AthleteDto> athletes = paymentService.septemberAthletes(1);

		Date startDate = dateFormat.parse("2021-09-01");
		athletes.addAll(athleteService.findByRegistrationDates(startDate, new Date()));

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(1, Calendar.DAY_OF_MONTH);

		List<ExcelDataDto> data = new ArrayList<>();

		ExcelDataDto header = new ExcelDataDto();
		header.getData().addAll(columns.parallelStream().map(d -> {
			return new ExcelValueDto(d, CellType.STRING);
		}).collect(Collectors.toList()));
		data.add(header);

		data.addAll(athletes.parallelStream().map(athlete -> {
			ExcelDataDto dataDto = new ExcelDataDto();
			// Referencia
			dataDto.getData().add(new ExcelValueDto(String.valueOf(athlete.getCode())));
			// Nombre
			dataDto.getData().add(new ExcelValueDto(
					athlete.getFirstSurname() + " " + athlete.getSecondSurname() + ", " + athlete.getName()));
			// IBAN
			dataDto.getData().add(new ExcelValueDto(athlete.getIban()));
			// BIC
			dataDto.getData().add(new ExcelValueDto("NOTPROVIDED"));
			// Pais
			dataDto.getData().add(new ExcelValueDto("ES"));
			// Residente
			dataDto.getData().add(new ExcelValueDto("S"));
			// Ref Mandato
			dataDto.getData().add(new ExcelValueDto(String.valueOf(athlete.getCode())));
			// Fecha Firma Mandato
			dataDto.getData().add(new ExcelValueDto("31/10/2009"));
			// Ref Adeudo
			dataDto.getData().add(new ExcelValueDto(query.getRefAdeudo()));
			// Importe
			dataDto.getData().add(new ExcelValueDto(String.valueOf(athlete.getPaymentAmount()), CellType.NUMERIC));
			// Concepto
			dataDto.getData().add(new ExcelValueDto("Observaciones"));
			// Tipo Adeudo
			dataDto.getData().add(new ExcelValueDto("RECUR"));
			// Fecha Cobro
			dataDto.getData().add(new ExcelValueDto(dateFormat.format(cal.getTime())));
			return dataDto;
		}).filter(Objects::nonNull).collect(Collectors.toList()));

		ByteArrayInputStream bas = ExcelUtils.generateExcel("Atletas", data);
		IOUtils.copy(bas, response.getOutputStream());

	}

}
