package com.backend.sprint.model.dao;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Immutable
@Table(name = "registered_athletes")
public class RegisteredAthleteDao extends GenericAthleteDao {

	@Id
	private long id;

	private String scheduleIds;
//	private long familiarNumber;

}
