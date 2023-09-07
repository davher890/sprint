package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.UnregisteredAthleteDao;

@Repository
public interface UnregisteredAthleteRepository extends PagingAndSortingRepository<UnregisteredAthleteDao, Long>,
		JpaSpecificationExecutor<UnregisteredAthleteDao> {

	@Query(value = "SELECT * FROM unregistered_athletes WHERE last_unregister_date < ?1 AND active = true", nativeQuery = true)
	public List<UnregisteredAthleteDao> findByLastUnregisterDate(Date lastUnregisterDate);

	// For receipts
	@Query(value = "SELECT * FROM unregistered_athletes WHERE sport_school_id = ?1 AND last_unregister_date > ?2 AND payment_type IN (?3) AND fee_type IN (?4) AND active = true", nativeQuery = true)
	public List<UnregisteredAthleteDao> findBySportSchoolIdAndLastUnregisterDate(long sportSchoolId,
			Date lastUnregisterDate, List<String> paymentTypes, List<String> feeTypes);

	@Query(value = "SELECT * FROM unregistered_athletes WHERE sport_school_id = ?1 AND (('territorial' IN (?3) AND license_territorial_fee > 0) OR ('national' IN (?3) AND license_national_fee > 0)) AND payment_type IN (?2) AND active = true", nativeQuery = true)
	public List<UnregisteredAthleteDao> findBySportSchoolIdAndLicense(long sportSchoolId, List<String> paymentTypes,
			List<String> licenseTypes);
}
