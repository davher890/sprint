package com.backend.sprint.model.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import com.opencsv.bean.CsvNumber;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteLicenseDto {

	@CsvBindByNames(value = { @CsvBindByName(column = "ESTADO", profiles = "update_licenses") })
	private String estado;

	@CsvBindByNames(value = { @CsvBindByName(column = "TIPO_LICENCIA", profiles = "update_licenses") })
	private String tipoLicencia;

	@CsvBindByNames(value = { @CsvBindByName(column = "ESTADO LICENCIA ASOCIADA", profiles = "update_licenses") })
	private String estadoLicenciaAsociada;

	@CsvBindByNames(value = { @CsvBindByName(column = "ROL", profiles = "update_licenses") })
	private String rol;

	@CsvBindByNames(value = { @CsvBindByName(column = "PACK", profiles = "update_licenses") })
	private String pack;

	@CsvBindByNames(value = { @CsvBindByName(column = "NUM_LICENCIA", profiles = "update_licenses") })
	private String numLicencia;

	@CsvBindByNames(value = { @CsvBindByName(column = "APELLIDO", profiles = "update_licenses") })
	private String apellido;

	@CsvBindByNames(value = { @CsvBindByName(column = "APELLIDO2", profiles = "update_licenses") })
	private String apellido2;

	@CsvBindByNames(value = { @CsvBindByName(column = "NOMBRE", profiles = "update_licenses") })
	private String nombre;

	@CsvBindByNames(value = { @CsvBindByName(column = "CLUB_FEDERA", profiles = "update_licenses") })
	private String clubFedera;

	@CsvBindByNames(value = { @CsvBindByName(column = "EMAIL_CLUB", profiles = "update_licenses") })
	private String emailClub;

	@CsvBindByNames(value = { @CsvBindByName(column = "CLUB", profiles = "update_licenses") })
	private String club;

	@CsvBindByNames(value = { @CsvBindByName(column = "FEDERACIÓN", profiles = "update_licenses") })
	private String federacion;

	@CsvBindByNames(value = { @CsvBindByName(column = "CATEGORÍA", profiles = "update_licenses") })
	private String categoria;

	@CsvBindByNames(value = { @CsvBindByName(column = "COLEGIO", profiles = "update_licenses") })
	private String colegio;

	@CsvBindByNames(value = { @CsvBindByName(column = "NUM_ID", profiles = "update_licenses") })
	private String numId;

	@CsvBindByNames(value = { @CsvBindByName(column = "CARNET_IMPRESO", profiles = "update_licenses") })
	private String carnetImpreso;

	@CsvBindByNames(value = { @CsvBindByName(column = "ENTRENADOR", profiles = "update_licenses") })
	private String entrenador;

	@CsvBindByNames(value = { @CsvBindByName(column = "GÉNERO", profiles = "update_licenses") })
	private String genero;

	@CsvBindByNames(value = { @CsvBindByName(column = "SOBRECOSTO", profiles = "update_licenses") })
	private String sobrecosto;

	@CsvBindByNames(value = { @CsvBindByName(column = "FOTO", profiles = "update_licenses") })
	private String foto;

	@CsvBindByNames(value = { @CsvBindByName(column = "FECHA_NACIMIENTO", profiles = "update_licenses") })
	private String fechaNacimiento;

	@CsvBindByNames(value = { @CsvBindByName(column = "DIRECCION", profiles = "update_licenses") })
	private String direccion;

	@CsvBindByNames(value = { @CsvBindByName(column = "LOCALIDAD", profiles = "update_licenses") })
	private String localidad;

	@CsvBindByNames(value = { @CsvBindByName(column = "CODIGO POSTAL", profiles = "update_licenses") })
	private String codigoPostal;

	@CsvBindByNames(value = { @CsvBindByName(column = "PROVINCIA", profiles = "update_licenses") })
	private String provincia;

	@CsvBindByNames(value = { @CsvBindByName(column = "TELEFONO", profiles = "update_licenses") })
	private String telefono;

	@CsvBindByNames(value = { @CsvBindByName(column = "MOVIL", profiles = "update_licenses") })
	private String movil;

	@CsvBindByNames(value = { @CsvBindByName(column = "NACIONALIDAD", profiles = "update_licenses") })
	private String nacionalidad;

	@CsvBindByNames(value = { @CsvBindByName(column = "TEMPORADA", profiles = "update_licenses") })
	private String temporada;

	@CsvBindByNames(value = { @CsvBindByName(column = "EMAIL", profiles = "update_licenses") })
	private String email;

	@CsvBindByNames(value = { @CsvBindByName(column = "DORSAL", profiles = "update_licenses") })
	private String dorsal;

	@CsvBindByNames(value = { @CsvBindByName(column = "FECHA_ALTA", profiles = "update_licenses") })
	private String fechaAlta;

	@CsvBindByNames(value = { @CsvBindByName(column = "FECHA_RENOVACIÓN", profiles = "update_licenses") })
	private String fechaRenovacion;

	@CsvBindByNames(value = { @CsvBindByName(column = "ULTIMO_PAGO", profiles = "update_licenses") })
	private String ultimoPago;

	@CsvBindByNames(value = { @CsvBindByName(column = "FECHA_ULTIMO_PAGO", profiles = "update_licenses") })
	private String fechaUltimoPago;

	@CsvBindByNames(value = { @CsvBindByName(column = "DELITOS", profiles = "update_licenses") })
	private String delitos;

	@CsvBindByNames(value = { @CsvBindByName(column = "EMPADRONAMIENTO", profiles = "update_licenses") })
	private String empadronamiento;

	@CsvBindByNames(value = { @CsvBindByName(column = "RESIDENCIA", profiles = "update_licenses") })
	private String residencia;

	@CsvBindByNames(value = { @CsvBindByName(column = "RECONOCIMIENTO", profiles = "update_licenses") })
	private String reconocimiento;

	@CsvBindByNames(value = { @CsvBindByName(column = "CUOTA_LICENCIA", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cuotaLicencia;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_SEGURO_DEP", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cSeguroDep;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_SEGURO_TRAI_LN", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cSeguroTraiLn;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_ADAPTACION_EUROPA", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cAdaptacionEuropa;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_ADAPTACION_MUNDIAL", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cAdaptacionMundial;

	@CsvBindByNames(value = { @CsvBindByName(column = "OBSERVACIONES", profiles = "update_licenses") })
	private String observaciones;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_PASO T A N", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cPasoTAN;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_INDEPENDIENTE", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cIndependiente;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_CLUB OTRA AUTONOMIA", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cClubOtraAutonomia;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_FUERA PLAZO", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cFueraPlazo;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_FUERA PLAZO INDE", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cFueraPlazoInde;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_CLUB A INDE", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cClubAInde;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_INDE A CLUB", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cIndeAClub;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_EMISION CARNET", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cEmisionCarnet;

	@CsvBindByNames(value = { @CsvBindByName(column = "C_ADAPTACION_TERRITORIAL", profiles = "update_licenses") })
	@CsvNumber("#0.0#")
	private float cAdaptacionTerritorial;

}
