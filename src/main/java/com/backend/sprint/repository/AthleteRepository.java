package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

	@Query(value = "SELECT * FROM athletes WHERE code = ?1 ", nativeQuery = true)
	public AthleteDao findByCode(long code);

	@Query(value = "SELECT * FROM athletes WHERE LOWER(first_surname) = LOWER(?1) AND LOWER(second_surname) = LOWER(?2) AND PHONE1 = ?3 AND PHONE2 = ?4 AND PHONE3 = ?4", nativeQuery = true)
	public List<AthleteDao> findRelatives(String firstSurname, String secondSurname, String phone1, String phone2,
			String phone3);

	@Query(value = "SELECT * FROM athletes WHERE LOWER(first_surname) = LOWER(?1) AND LOWER(second_surname) = LOWER(?2)", nativeQuery = true)
	public List<AthleteDao> findBySurnames(String firstSurname, String secondSurname);

	@Query(value = "SELECT MAX(code) FROM athletes", nativeQuery = true)
	public long findLastFamilyCode();

	@Query(value = "SELECT * FROM athletes WHERE id != ?1 AND name = ?2 AND first_surname = ?3 AND second_surname = ?4 AND birth_date = ?5 ", nativeQuery = true)
	public List<AthleteDao> findByUniqueColumns(long id, String name, String firstSurname, String secondSurname,
			Date birthDate);
}
