package com.backend.sprint.repository;

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

	@Query(value = "SELECT * FROM athletes WHERE first_surname = ?1 AND second_surname = ?2 AND PHONE1 = ?3 AND PHONE2 = ?4 AND PHONE3 = ?4", nativeQuery = true)
	public List<AthleteDao> findRelatives(String firstSurname, String secondSurname, String phone1, String phone2,
			String phone3);

	@Query(value = "SELECT * FROM athletes WHERE first_surname = ?1 AND second_surname = ?2", nativeQuery = true)
	public List<AthleteDao> findBySurnames(String firstSurname, String secondSurname);

	@Query(value = "SELECT MAX(code) FROM athletes", nativeQuery = true)
	public long findLastFamilyCode();
}
