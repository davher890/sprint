package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.RegisteredAthleteDao;

public interface RegisteredAthleteRepository
		extends PagingAndSortingRepository<RegisteredAthleteDao, Long>, JpaSpecificationExecutor<RegisteredAthleteDao> {

	@Query(value = "SELECT * FROM registered_athletes WHERE group_id = ?1 AND active = true ORDER BY first_surname, second_surname", nativeQuery = true)
	public List<RegisteredAthleteDao> findByGroupId(long groupId);

}
