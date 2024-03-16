package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.ReceiptDao;

@Repository
public interface ReceiptRepository
		extends PagingAndSortingRepository<ReceiptDao, Long>, JpaSpecificationExecutor<ReceiptDao> {

	@Query(value = "SELECT COALESCE(SUM(enrollment_fee), 0) FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	public float getSumEnrollmentFee(long receiptId);

	@Query(value = "SELECT COALESCE(SUM(membership_fee), 0) FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	public float getSumMembershipFee(long receiptId);

	@Query(value = "SELECT COALESCE(SUM(sport_school_annual_fee), 0) FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	public float getSumSportSchoolAnnualFee(long receiptId);

	@Query(value = "SELECT COALESCE(SUM(monthly_fee), 0) FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	public float getSumMonthlyFee(long receiptId);

	@Query(value = "SELECT COALESCE(SUM(license_national_fee), 0) + COALESCE(SUM(license_territorial_fee), 0) + COALESCE(SUM(license_other_fee), 0) FROM receipt_athletes WHERE receipt_id = ?1", nativeQuery = true)
	public float getSumLicenseFee(long receiptId);
}
