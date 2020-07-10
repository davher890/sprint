package com.backend.sprint.model.dao;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
@Table(name = "schedules")
public class ScheduleDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToMany(mappedBy = "schedules")
	private Set<GroupDao> groups;

	@NotNull
	private String day;

	private int startHour;

	private int startMinute;

	private int endHour;

	private int endMinute;

	@CreationTimestamp
	private Date createdAt;

	@UpdateTimestamp
	private Date updatedAt;

}
