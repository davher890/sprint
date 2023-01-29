package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.FeeDao;

@Repository
public interface FeeRepository extends PagingAndSortingRepository<FeeDao, Long>, JpaSpecificationExecutor<FeeDao> {

	@Query(value = "SELECT * FROM fees WHERE sport_school_id = ?1", nativeQuery = true)
	public FeeDao findBySportSchoolId(long sportSchoolId);

}
