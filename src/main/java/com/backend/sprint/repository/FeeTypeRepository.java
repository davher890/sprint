package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.FeeTypeDao;

@Repository
public interface FeeTypeRepository
		extends PagingAndSortingRepository<FeeTypeDao, Long>, JpaSpecificationExecutor<FeeTypeDao> {

	@Query(value = "SELECT ft.* FROM fee_types ft INNER JOIN sport_schools_fee_types ssft ON ft.id = ssft.fee_type_id WHERE ssft.sport_school_id  = ?1 ORDER BY ft.name", nativeQuery = true)
	public List<FeeTypeDao> findBySportSchoolId(long sportSchoolId);
}
