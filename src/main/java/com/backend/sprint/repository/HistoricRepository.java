package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.HistoricDao;

@Repository
public interface HistoricRepository
		extends PagingAndSortingRepository<HistoricDao, Long>, JpaSpecificationExecutor<HistoricDao> {

	@Query(value = "SELECT * FROM historic WHERE athlete_id = ?1 ORDER BY date DESC ", nativeQuery = true)
	public Set<HistoricDao> findAthleteRegistration(long athleteId);
}
