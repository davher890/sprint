package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.FamilyDao;

@Repository
public interface FamilyRepository
		extends PagingAndSortingRepository<FamilyDao, Long>, JpaSpecificationExecutor<FamilyDao> {

	@Query(value = "SELECT * FROM families WHERE first_surname = ?1 AND second_surname = ?2", nativeQuery = true)
	public FamilyDao findBySurnames(String firstSurname, String secondSurname);

	@Query(value = "SELECT * FROM families WHERE code = ?1", nativeQuery = true)
	public FamilyDao findByCode(long code);
}
