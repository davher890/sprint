package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.FamilyDao;

@Repository
public interface FamilyRepository
		extends PagingAndSortingRepository<FamilyDao, Long>, JpaSpecificationExecutor<FamilyDao> {

	@Query(value = "SELECT * FROM families WHERE familiar_one_surname = ?1 AND familiar_two_surname = ?2", nativeQuery = true)
	public List<FamilyDao> findBySurnames(String firstSurname, String secondSurname);

	@Query(value = "SELECT * FROM families WHERE code = ?1", nativeQuery = true)
	public FamilyDao findByCode(long code);

	@Query(value = "SELECT MAX(code) FROM families", nativeQuery = true)
	public long findLastCode();
}
