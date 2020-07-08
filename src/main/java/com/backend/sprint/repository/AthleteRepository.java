package com.backend.sprint.repository;

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
}
