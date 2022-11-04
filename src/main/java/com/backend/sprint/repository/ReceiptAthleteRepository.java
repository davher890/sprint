package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.ReceiptAthleteDao;

@Repository
public interface ReceiptAthleteRepository
		extends PagingAndSortingRepository<ReceiptAthleteDao, Long>, JpaSpecificationExecutor<ReceiptAthleteDao> {

	public List<ReceiptAthleteDao> findByReceiptId(long receiptId);

	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true AND rs.status = ?2 WHERE ras.athlete_id = ?1 ", nativeQuery = true)
	public List<ReceiptAthleteDao> findByAthleteIdAndStatus(long receiptId, String status);

	@Modifying
	@Query(value = "DELETE FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	void deleteByReceiptId(long receiptId);

	@Modifying
	@Query(value = "DELETE FROM receipt_athletes WHERE receipt_id = ?1 AND athlete_id IN ?2", nativeQuery = true)
	void deleteByAthleteIds(long receiptId, List<Long> athleteIds);
}
