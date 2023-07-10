package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;
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

	@Query(value = "SELECT * FROM athletes WHERE code = ?1 AND active = true", nativeQuery = true)
	public AthleteDao findByCode(long code);

	@Query(value = "SELECT * FROM athletes WHERE LOWER(first_surname) = LOWER(?1) AND LOWER(second_surname) = LOWER(?2) AND sport_school_id IN ?3 AND active = true", nativeQuery = true)
	public List<AthleteDao> findBySurnames(String firstSurname, String secondSurname, Set<Long> sportSchoolIds);

	@Query(value = "SELECT MAX(code) FROM athletes", nativeQuery = true)
	public long findLastFamilyCode();

	@Query(value = "SELECT * FROM athletes a1 INNER JOIN "
			+ "(SELECT id FROM athletes WHERE (phone1 IN (?1) OR phone2 IN (?1) OR phone3 IN (?1) AND sport_school_id IN ?2) AND active = true GROUP by 1) a2 "
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
}
