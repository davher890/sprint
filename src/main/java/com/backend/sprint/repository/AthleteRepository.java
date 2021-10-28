package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.AthleteDao;

@Repository
public interface AthleteRepository
		extends PagingAndSortingRepository<AthleteDao, Long>, JpaSpecificationExecutor<AthleteDao> {

	@Query(value = "SELECT * FROM athletes WHERE family_id = ?1 ", nativeQuery = true)
	public Set<AthleteDao> findByFamily(long familyId);

	@Query(value = "SELECT * FROM athletes a INNER JOIN families f ON f.id = a.family_id WHERE f.code = ?1 ", nativeQuery = true)
	public List<AthleteDao> findByFamilyCode(long familyCode);

	@Query(value = "SELECT * FROM athletes WHERE code = ?1 ", nativeQuery = true)
	public AthleteDao findByCode(long code);

	@Query(value = "SELECT * FROM athletes WHERE LOWER(first_surname) = LOWER(?1) AND LOWER(second_surname) = LOWER(?2)", nativeQuery = true)
	public List<AthleteDao> findBySurnames(String firstSurname, String secondSurname);

	@Query(value = "SELECT MAX(code) FROM athletes", nativeQuery = true)
	public long findLastFamilyCode();

	@Query(value = "SELECT * FROM athletes a1 INNER JOIN "
			+ "(SELECT id FROM athletes WHERE phone1 IN (?1) OR phone2 IN (?1) OR phone3 IN (?1) GROUP by 1) a2 "
			+ "ON a1.id = a2.id", nativeQuery = true)
	public List<AthleteDao> findByPhoneList(List<String> phoneList);

	@Query(value = "SELECT * FROM athletes WHERE id != ?1 AND name = ?2 AND first_surname = ?3 AND second_surname = ?4 "
			+ "AND EXTRACT(YEAR FROM birth_date) = ?5 AND EXTRACT(MONTH FROM birth_date) = ?6 AND EXTRACT(DAY FROM birth_date) = ?7 ", nativeQuery = true)
	public List<AthleteDao> findByIdAndUniqueColumns(long id, String name, String firstSurname, String secondSurname,
			int year, int month, int day);

	@Query(value = "SELECT * FROM athletes WHERE name = ?1 AND first_surname = ?2 AND second_surname = ?3 "
			+ "AND EXTRACT(YEAR FROM birth_date) = ?4 AND EXTRACT(MONTH FROM birth_date) = ?5 AND EXTRACT(DAY FROM birth_date) = ?6 ", nativeQuery = true)
	public List<AthleteDao> findByUniqueColumns(String name, String firstSurname, String secondSurname, int year,
			int month, int day);

	@Query(value = "SELECT * FROM athletes WHERE name = ?1 AND first_surname = ?2 AND second_surname = ?3 "
			+ "AND EXTRACT(YEAR FROM birth_date) = ?4 AND EXTRACT(MONTH FROM birth_date) = ?5 AND EXTRACT(DAY FROM birth_date) = ?6 ", nativeQuery = true)
	public List<AthleteDao> findByNameColumns(String name, String firstSurname, String secondSurname, int year,
			int month, int day);

	@Query(value = "SELECT * FROM athletes WHERE id IN (SELECT DISTINCT(athlete_id) FROM athlete_group_schedules WHERE group_id = ?1 AND schedule_id IN (?2))", nativeQuery = true)
	public Page<AthleteDao> findByGroupAndSchedulesPagination(long groupId, List<Long> scheduleIds,
			Specification<AthleteDao> specification, Pageable pageable);

	@Query(value = "SELECT * FROM athletes WHERE id IN (SELECT DISTINCT(athlete_id) FROM athlete_group_schedules WHERE group_id = ?1 AND schedule_id IN (?2))", nativeQuery = true)
	public List<AthleteDao> findByGroupAndSchedules(long groupId, List<Long> scheduleIds);

	// Returns the athletes in the historic
	@Query(value = "SELECT * FROM athletes WHERE NOT("
			+ "(last_register_date IS NULL AND (last_unregister_date IS NULL OR last_unregister_date > NOW())) "
			+ " OR "
			+ "(last_register_date IS NOT NULL AND last_register_date <= NOW() AND (last_unregister_date IS NULL OR last_unregister_date < last_register_date)))", nativeQuery = true)
	public List<AthleteDao> findAllHistoric();

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

	@Query(value = "SELECT * FROM athletes a "
			+ "LEFT JOIN historic h ON a.id = h.athlete_id WHERE h.id is null AND a.last_register_date < ?1 ", nativeQuery = true)
	public List<AthleteDao> findNewAthletesBeforeDate(Date date);

}
