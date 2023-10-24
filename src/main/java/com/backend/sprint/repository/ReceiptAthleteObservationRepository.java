package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.ReceiptAthleteObservationDao;

@Repository
public interface ReceiptAthleteObservationRepository
		extends PagingAndSortingRepository<ReceiptAthleteObservationDao, Long>,
		JpaSpecificationExecutor<ReceiptAthleteObservationDao> {

	@Query(value = "SELECT * FROM receipt_athlete_observations WHERE receipt_athlete_id = ?1 AND active = true ORDER BY created_at DESC ", nativeQuery = true)
	public Set<ReceiptAthleteObservationDao> findReceiptAthleteObservations(long receiptAthleteId);
}
