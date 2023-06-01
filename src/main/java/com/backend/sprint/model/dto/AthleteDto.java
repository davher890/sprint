package com.backend.sprint.model.dto;

import java.util.Date;
import java.util.Set;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvDates;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteDto {

	private long id;

	private long sportSchoolId;
	private String sportSchoolName;

	private long familyId;
	@CsvBindByName(column = "CODIGO FAMILIAR")
	private long familyCode;

	private long groupId;
	private String groupName;

	private Set<Long> scheduleIds;
	private Set<ScheduleDto> schedules;
	private String days;
	private Integer numDays;

	private boolean imageAuth;

	// Personal info
	@CsvBindByName(column = "NOMBRE")
	private String name;
	@CsvBindByName(column = "PRIMER APELLIDO")
	private String firstSurname;
	@CsvBindByName(column = "SEGUNDO APELLIDO")
	private String secondSurname;
	@CsvBindByName(column = "FECHA DE NACIMIENTO")
	@CsvDates(value = { @CsvDate(value = "yyyy/MM/dd"), @CsvDate(value = "dd/MM/yyyy") })
	private Date birthDate;
	@CsvBindByName(column = "DNI(Atleta)")
	private String dni;
	@CsvBindByName(column = "SEXO")
	private String gender;
	@CsvBindByName(column = "Nacionalidad")
	private String nationality;
	@CsvBindByName(column = "Lugar de Nacimiento")
	private String birthPlace;

	// Family info
	@CsvBindByName(column = "NOMBRE DEL PADRE O TUTOR")
	private String familiarOneName;
	@CsvBindByName(column = "Fam.1 1er.APELLIDO")
	private String familiarOneFirstSurname;
	@CsvBindByName(column = "Fam.1 2o.APELLIDO")
	private String familiarOneSecondSurname;
	@CsvBindByName(column = "Fam.1 Dni")
	private String familiarOneDni;
	@CsvBindByName(column = "email 2")
	private String familiarOneMail;

	@CsvBindByName(column = "NOMBRE DE LA MADRE O TUTORA")
	private String familiarTwoName;
	@CsvBindByName(column = "Fam.2 1er.APELLIDO")
	private String familiarTwoFirstSurname;
	@CsvBindByName(column = "Fam.2 2o.APELLIDO")
	private String familiarTwoSecondSurname;
	@CsvBindByName(column = "Fam.2 Dni")
	private String familiarTwoDni;
	@CsvBindByName(column = "email 3")
	private String familiarTwoMail;

	// Sport info
	@CsvBindByName(column = "Categoría")
	private String category;
	@CsvBindByName(column = "CAT DORSAL")
	private String dorsalCategory;
	@CsvBindByName(column = "DORSAL")
	private long dorsalNumber;
	@CsvBindByName(column = "Licencia")
	private String license;
	@CsvBindByName(column = "Tipo de licencia")
	private String licenseType;
	private boolean specialization;

	// Contact info
	@CsvBindByName(column = "email 1")
	private String mail;
	@CsvBindByName(column = "TFNO.")
	private String phone1;
	@CsvBindByName(column = "TFNO. 2")
	private String phone2;
	@CsvBindByName(column = "TFNO. 3")
	private String phone3;
	@CsvBindByName(column = "MUNICIPIO")
	private String municipality;
	@CsvBindByName(column = "CP")
	private String postalCode;
	@CsvBindByName(column = "DOMICILIO")
	private String address;

	// Bank info
	@CsvBindByName(column = "IBAN")
	private String iban;
	@CsvBindByName(column = "FP")
	private String paymentType;
	@CsvBindByName(column = "TIPO CUOTA")
	private String feeType;
	@CsvBindByName(column = "TITULAR DE LA CUENTA")
	private String holderName;
	@CsvBindByName(column = "1er APELLIDO Tit. Cuenta")
	private String holderFirstSurname;
	@CsvBindByName(column = "2o APELLIDO Tit. Cuenta")
	private String holderSecondSurname;
	@CsvBindByName(column = "DNI AUTORIZADO CUENTA")
	private String holderDni;

	private Set<HistoricDto> historic;

	private Set<ObservationDto> observations;

	// Fee
	private FeeDto fee;// Fee

	private float paymentAmount;

	private boolean enrollmentFeePaid = false;
	private boolean membershipFeePaid = false;

	// Code
	@CsvBindByName(column = "CODIGO ATLETA")
	private long code;
	private long familiarNumber;

	private Date lastRegisterDate;
	private Date lastActivationDate;

	private Date lastUnregisterDate;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private String errorMessage;

	private long updatedById;

}
