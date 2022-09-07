package com.backend.sprint.model.dao;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(name = "schedules", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "day", "startHour", "startMinute", "endHour", "endMinute" }) })
public class ScheduleDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToMany(mappedBy = "schedules", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<GroupDao> groups;

	@ManyToMany(mappedBy = "schedules", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AthleteDao> athletes;

	@NotNull
	private String day;

	@NotNull
	private int dayNumber;

	private int startHour;

	private int startMinute;

	private int endHour;

	private int endMinute;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private UserDao updatedBy;

}
