package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.SportdataDao;

public interface SportdataRepository
		extends PagingAndSortingRepository<SportdataDao, Long>, JpaSpecificationExecutor<SportdataDao> {

	@Query(value = "SELECT * FROM sportdata WHERE athlete_id = ?1", nativeQuery = true)
	public List<SportdataDao> findByAthleteId(long athleteId);
}
