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

	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true WHERE ras.athlete_id = ?1 ", nativeQuery = true)
	public List<ReceiptAthleteDao> findByAthleteId(long receiptId);

	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true AND rs.status = ?2 AND rs.season = ?3 WHERE ras.athlete_id = ?1 ", nativeQuery = true)
	public List<ReceiptAthleteDao> findByAthleteIdAndReceiptStatusAndSeason(long receiptId, String status,
			String season);

//	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN registered_athletes ra on ra.id = ras.athlete_id INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true AND ras.status = ?1 AND rs.season = ?2", nativeQuery = true)
//	public List<ReceiptAthleteDao> findByReceiptStatusAndSeasonAndIsRegistered(String status, String season);

	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN unregistered_athletes ra on ra.id = ras.athlete_id INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true AND ras.status = ?1 AND rs.season = ?2", nativeQuery = true)
	public List<ReceiptAthleteDao> findByReceiptStatusAndSeasonAndIsUnregistered(String status, String season);

	@Query(value = "SELECT ras.* FROM receipt_athletes ras INNER JOIN receipts rs ON rs.id = ras.receipt_id AND rs.active = true AND rs.status = ?2 WHERE ras.athlete_id = ?1 ", nativeQuery = true)
	public List<ReceiptAthleteDao> findByAthleteIdAndReceiptStatus(long athleteId, String status);

	@Query(value = "SELECT * FROM receipt_athletes WHERE athlete_id = ?1 AND receipt_id = ?2", nativeQuery = true)
	public ReceiptAthleteDao findByAthleteIdAndReceiptId(long athleteId, long receiptId);

	@Modifying
	@Query(value = "UPDATE receipt_athletes SET status = 'paid' WHERE receipt_id = ?1", nativeQuery = true)
	void validateReceiptId(long receiptId);

	@Modifying
	@Query(value = "UPDATE athletes a SET membership_fee_paid = true FROM receipt_athletes ra WHERE ra.athlete_id = a.id AND ra.receipt_id = ?1", nativeQuery = true)
	public void athleteMembershipFeePaid(long receiptId);

	@Modifying
	@Query(value = "UPDATE athletes a SET enrollment_fee_paid = true FROM receipt_athletes ra WHERE ra.athlete_id = a.id AND ra.receipt_id = ?1", nativeQuery = true)
	public void ahtleteEnrollmentFeePaid(long receiptId);

	@Modifying
	@Query(value = "UPDATE athletes a SET sport_school_annual_fee_paid = true FROM receipt_athletes ra WHERE ra.athlete_id = a.id AND ra.receipt_id = ?1", nativeQuery = true)
	public void ahtleteSportSchoolAnnualFeePaid(long receiptId);

	@Modifying
	@Query(value = "DELETE FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	void deleteByReceiptId(long receiptId);

	@Modifying
	@Query(value = "DELETE FROM receipt_athletes WHERE receipt_id = ?1 AND athlete_id IN ?2", nativeQuery = true)
	void deleteByAthleteIds(long receiptId, List<Long> athleteIds);

}
