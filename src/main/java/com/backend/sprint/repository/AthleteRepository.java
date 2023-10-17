package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.AthleteDao;

@Repository
public interface AthleteRepository
		extends PagingAndSortingRepository<AthleteDao, Long>, JpaSpecificationExecutor<AthleteDao> {

	@Query(value = "SELECT * FROM athletes a INNER JOIN families f ON f.id = a.family_id WHERE f.code = ?1 AND active = true", nativeQuery = true)
	public List<AthleteDao> findByFamilyCode(long familyCode);

	@Query(value = "SELECT * FROM athletes WHERE family_id = ?1 AND active = true", nativeQuery = true)
	public List<AthleteDao> findByFamilyId(long familyId);

	@Query(value = "SELECT * FROM athletes WHERE code = ?1 AND active = true", nativeQuery = true)
	public AthleteDao findByCode(long code);

	@Query(value = "SELECT * FROM athletes WHERE LOWER(first_surname) = LOWER(?1) AND LOWER(second_surname) = LOWER(?2) AND sport_school_id IN ?3 AND active = true", nativeQuery = true)
	public List<AthleteDao> findBySurnames(String firstSurname, String secondSurname, Set<Long> sportSchoolIds);

	@Query(value = "SELECT MAX(code) FROM athletes", nativeQuery = true)
	public long findLastFamilyCode();

	@Query(value = "SELECT * FROM athletes a1 INNER JOIN "
			+ "(SELECT id FROM athletes WHERE (phone1 IN (?1) OR phone2 IN (?1) OR phone3 IN (?1) AND sport_school_id IN ?2) AND active = true GROUP by id) a2 "
			+ "ON a1.id = a2.id", nativeQuery = true)
	public List<AthleteDao> findByPhoneList(List<String> phoneList, Set<Long> sportSchoolIds);

	@Query(value = "SELECT * FROM athletes WHERE id IN (SELECT DISTINCT(a.id) "
			+ "FROM athletes a INNER JOIN groups_schedules gs ON gs.group_id = a.group_id "
			+ "WHERE a.group_id = ?1 AND gs.schedule_id IN (?2)) AND active = true", nativeQuery = true)
	public List<AthleteDao> findByGroupAndSchedules(long groupId, List<Long> scheduleIds);

	// Returns the registered athletes
	@Query(value = "SELECT * FROM athletes WHERE "
			+ "(last_register_date IS NULL AND (last_unregister_date IS NULL OR last_unregister_date > NOW())) "
			+ " OR "
			+ "(last_register_date IS NOT NULL AND last_register_date <= NOW() AND (last_unregister_date IS NULL OR last_unregister_date < last_register_date))", nativeQuery = true)
	public List<AthleteDao> findAllRegistered();

	@Query(value = "SELECT * FROM athletes a "
			+ "INNER JOIN (SELECT *, row_number() OVER (partition by athlete_id order by date desc) AS rn FROM historic WHERE date BETWEEN ?1 AND ?2) h ON a.id = h.athlete_id "
			+ "WHERE rn = 1 AND h.type = 'REGISTER'", nativeQuery = true)
	public List<AthleteDao> findByRegistrationDates(Date startDate, Date endDate);

	@Query(value = "SELECT * FROM athletes WHERE group_id = ?1 AND active = true ORDER BY first_surname, second_surname", nativeQuery = true)
	public List<AthleteDao> findByGroupId(long groupId);

	@Modifying
	@Query(value = "UPDATE athletes SET group_id = NULL WHERE group_id = ?1", nativeQuery = true)
	public void deleteGroupAthletes(long groupId);

	@Modifying
	@Query(value = "UPDATE athletes a SET group_id = null FROM unregistered_athletes u WHERE u.id = a.id AND a.group_id IS NOT null", nativeQuery = true)
	public void deleteGroupUnregisteredAthletes();

	@Query(value = "SELECT * FROM athletes WHERE BTRIM(LTRIM(LOWER(dni),'0')) = BTRIM(LTRIM(LOWER(?1),'0'))", nativeQuery = true)
	public List<AthleteDao> findByDni(String dni);

	@Query(value = "SELECT * FROM athletes WHERE BTRIM(LOWER(UNACCENT(name))) = BTRIM(LOWER(UNACCENT(?1))) AND BTRIM(LOWER(UNACCENT(first_surname))) = BTRIM(LOWER(UNACCENT(?2))) AND BTRIM(LOWER(UNACCENT(second_surname))) = BTRIM(LOWER(UNACCENT(?3)))", nativeQuery = true)
	public List<AthleteDao> findByName(String name, String firstSurname, String secondSurname);

	// For receipts
	@Query(value = "SELECT ath.* FROM athletes ath INNER JOIN ("
			+ "SELECT a.code, h.date, h.type, h.athlete_id, ROW_NUMBER() OVER ( PARTITION BY athlete_id ORDER BY date DESC) rn, DATE_PART('day', now() - h.date) as trial_days "
			+ "FROM athletes a INNER JOIN historic h on a.id = h.athlete_id WHERE sport_school_id = ?1 "
			+ "AND date < ?2 AND payment_type IN (?3) AND fee_type IN (?4) "
			+ "AND active = true ) his on his.athlete_id = ath.id AND trial_days > 15 AND rn = 1 AND type = 'REGISTER'", nativeQuery = true)
	public List<AthleteDao> findBySportSchoolIdAndLastRegisterDate(long sportSchoolId, Date lastRegisterDate,
			List<String> paymentTypes, List<String> feeTypes);

	// Get active athletes by date
	@Query(value = "SELECT date_limit, COUNT(athlete_id) "
			+ "FROM (SELECT h.athlete_id, h.type, t.date as date_limit, row_number() OVER (PARTITION BY h.athlete_id, t.date order by h.date desc, type desc) AS rn "
			+ "FROM historic h "
			+ "CROSS JOIN (SELECT CURRENT_DATE + i AS date FROM GENERATE_SERIES(date(?2) - CURRENT_DATE, date(?3) - CURRENT_DATE) i) t WHERE h.date <= t.date ) r "
			+ "INNER JOIN athletes a ON a.id = r.athlete_id AND active = true AND sport_school_id = ?1 "
			+ "WHERE rn = 1 AND type = 'REGISTER' AND active = true "
			+ "GROUP BY 1 ORDER BY 1 DESC;", nativeQuery = true)
	public List<Map<String, Integer>> findRegisteredAthletesByDate(long sportSchoolId, String startDate,
			String endDate);

}
