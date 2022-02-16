package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.ObservationDao;

@Repository
public interface ObservationRepository
		extends PagingAndSortingRepository<ObservationDao, Long>, JpaSpecificationExecutor<ObservationDao> {

	@Query(value = "SELECT * FROM observations WHERE athlete_id = ?1 ORDER BY created_at DESC ", nativeQuery = true)
	public Set<ObservationDao> findAthleteObservations(long athleteId);
}
