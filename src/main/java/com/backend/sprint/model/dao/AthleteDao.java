package com.backend.sprint.model.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "athletes")
public class AthleteDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "sport_school_id")
	private SportSchoolDao sportSchool;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "fee_type_id")
	private FeeTypeDao feeType;

	private String membershipType;

	private boolean imageAuth;

	// Personal info
	@NotNull
	private String name;
	private String firstSurname;
	private String secondSurname;
	private LocalDate birthDate;
	private String dni;
	private String gender;
	private String nationality;
	private String birthPlace;

	// Family info
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "family_id")
	private FamilyDao family;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private GroupDao group;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(joinColumns = @JoinColumn(name = "athlete_id"), inverseJoinColumns = @JoinColumn(name = "schedule_id"))
	private Set<ScheduleDao> schedules;

	@OneToMany(mappedBy = "athlete", fetch = FetchType.LAZY)
	private Set<ReceiptAthleteDao> receipts;

	private String familiarOneName;
	private String familiarOneFirstSurname;
	private String familiarOneSecondSurname;
	private String familiarOneDni;
	private String familiarOneMail;

	private String familiarTwoName;
	private String familiarTwoFirstSurname;
	private String familiarTwoSecondSurname;
	private String familiarTwoDni;
	private String familiarTwoMail;

	// Sport info
	private String category;
	private String dorsalCategory;
	private long dorsalNumber;
	private String license;
	private String licenseType;
	@Column(columnDefinition = "float default 0")
	private float licenseNationalFee;
	@Column(columnDefinition = "float default 0")
	private float licenseTerritorialFee;
	@Column(columnDefinition = "float default 0")
	private float licenseOtherFee;
	private String trackHours;

	// Contact info
	private String mail;
	private String phone1;
	private String phone2;
	private String phone3;
	private String municipality;
	private String postalCode;
	private String address;

	// Bank info
	private String iban;
	private String paymentType;
	private String holderName;
	private String holderFirstSurname;
	private String holderSecondSurname;
	private String holderDni;

	@OneToMany(mappedBy = "athlete", fetch = FetchType.LAZY)
	private Set<HistoricDao> historic;

	@OneToMany(mappedBy = "athlete", fetch = FetchType.LAZY)
	private Set<ObservationDao> observations;

	@OneToMany(mappedBy = "athlete", fetch = FetchType.LAZY)
	private Set<SportdataDao> sportdatas;

	@NotNull
	private long code;

	@Column(columnDefinition = "boolean default false")
	private boolean enrollmentFeePaid;

	@Column(columnDefinition = "boolean default false")
	private boolean sportSchoolAnnualFeePaid;

	@Column(columnDefinition = "boolean default false")
	private boolean membershipFeePaid;

	@Column(columnDefinition = "boolean default true")
	private boolean active;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private UserDao updatedBy;

}
