package com.backend.sprint.model.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvDates;
import com.opencsv.bean.CsvNumber;

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
	@CsvBindByNames(value = { @CsvBindByName(column = "PRIMER APELLIDO"),
			@CsvBindByName(column = "APELLIDO", profiles = "update_licenses") })
	private String firstSurname;
	@CsvBindByNames(value = { @CsvBindByName(column = "SEGUNDO APELLIDO"),
			@CsvBindByName(column = "APELLIDO2", profiles = "update_licenses") })
	private String secondSurname;
	@CsvBindByName(column = "FECHA DE NACIMIENTO")
	@CsvDates(value = { @CsvDate(value = "yyyy/MM/dd"), @CsvDate(value = "dd/MM/yyyy") })
	private LocalDate birthDate;
	@CsvBindByNames(value = { @CsvBindByName(column = "DNI(Atleta)"),
			@CsvBindByName(column = "NUM_ID", profiles = "update_licenses") })
	private String dni;
	@CsvBindByName(column = "SEXO")
	private String gender;
	@CsvBindByName(column = "Nacionalidad")
	private String nationality;
	@CsvBindByName(column = "Lugar de Nacimiento")
	private String birthPlace;

	// Family info
	@CsvBindByNames(value = { @CsvBindByName(column = "NOMBRE DEL PADRE O TUTOR"),
			@CsvBindByName(column = "NOMBRE TUTOR 1", profiles = "update_relatives") })
	private String familiarOneName;
	@CsvBindByNames(value = { @CsvBindByName(column = "Fam.1 1er.APELLIDO"),
			@CsvBindByName(column = "PRIMER APELLIDO TUTOR 1", profiles = "update_relatives") })
	private String familiarOneFirstSurname;
	@CsvBindByNames(value = { @CsvBindByName(column = "Fam.1 2o.APELLIDO"),
			@CsvBindByName(column = "SEGUNDO APELLIDO TUTOR 1", profiles = "update_relatives") })
	private String familiarOneSecondSurname;
	@CsvBindByName(column = "Fam.1 Dni")
	private String familiarOneDni;
	@CsvBindByName(column = "email 2")
	private String familiarOneMail;

	@CsvBindByNames(value = { @CsvBindByName(column = "NOMBRE DE LA MADRE O TUTORA"),
			@CsvBindByName(column = "NOMBRE TUTOR 2", profiles = "update_relatives") })
	private String familiarTwoName;
	@CsvBindByNames(value = { @CsvBindByName(column = "Fam.2 1er.APELLIDO"),
			@CsvBindByName(column = "PRIMER APELLIDO TUTOR 2", profiles = "update_relatives") })
	private String familiarTwoFirstSurname;
	@CsvBindByNames(value = { @CsvBindByName(column = "Fam.2 2o.APELLIDO"),
			@CsvBindByName(column = "SEGUNDO APELLIDO TUTOR 2", profiles = "update_relatives") })
	private String familiarTwoSecondSurname;
	@CsvBindByName(column = "Fam.2 Dni")
	private String familiarTwoDni;
	@CsvBindByName(column = "email 3")
	private String familiarTwoMail;

	// Sport info
	@CsvBindByName(column = "Categoría")
	private String category;
	@CsvBindByNames(value = { @CsvBindByName(column = "CAT DORSAL"),
			@CsvBindByName(column = "CATEGORÍA", profiles = "update_licenses") })
	private String dorsalCategory;
	@CsvBindByName(column = "DORSAL", profiles = "update_licenses")
	private long dorsalNumber;
	@CsvBindByNames(value = { @CsvBindByName(column = "Licencia"),
			@CsvBindByName(column = "NUM_LICENCIA", profiles = "update_licenses") })
	private String license;
	@CsvBindByNames(value = { @CsvBindByName(column = "Tipo de licencia"),
			@CsvBindByName(column = "TIPO_LICENCIA", profiles = "update_licenses") })
	private String licenseType;
	@CsvBindByNames(value = { @CsvBindByName(column = "CUOTA_LICENCIA", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float licenseNationalFee;
	@CsvBindByNames(value = { @CsvBindByName(column = "CUOTA_LICENCIA", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float licenseTerritorialFee;
	private boolean specialization;
	private String trackHours;

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

//	private String feeType;
	private long feeTypeId;
	@CsvBindByName(column = "TIPO CUOTA")
	private String feeTypeName;

	@CsvBindByName(column = "TITULAR DE LA CUENTA")
	private String holderName;
	@CsvBindByName(column = "1er APELLIDO Tit. Cuenta")
	private String holderFirstSurname;
	@CsvBindByName(column = "2o APELLIDO Tit. Cuenta")
	private String holderSecondSurname;
	@CsvBindByName(column = "DNI AUTORIZADO CUENTA")
	private String holderDni;

	private List<HistoricDto> historic;

	private Set<ObservationDto> observations;

	// Fee
	private FeeDto fee;// Fee

	private float paymentAmount;

	private boolean enrollmentFeePaid = false;
	private boolean membershipFeePaid = false;
	private boolean sportSchoolAnnualFeePaid = false;
	private boolean licenseFeePaid = false;

	// Code
	@CsvBindByNames(value = { @CsvBindByName(column = "CODIGO ATLETA"),
			@CsvBindByName(column = "COD ATLETA", profiles = "update_relatives") })
	private long code;
	private long familiarNumber;

	private LocalDate lastRegisterDate;

	private LocalDate lastUnregisterDate;

	private boolean active = true;

	private Date createdAt;

	private Date updatedAt;

	private String errorMessage;

	private long updatedById;

}
