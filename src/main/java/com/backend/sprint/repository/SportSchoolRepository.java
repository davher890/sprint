package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.SportSchoolDao;

public interface SportSchoolRepository
		extends PagingAndSortingRepository<SportSchoolDao, Long>, JpaSpecificationExecutor<SportSchoolDao> {

	@Query(value = "SELECT * FROM sport_schools WHERE name = ?1 ", nativeQuery = true)
	SportSchoolDao findByName(String name);

}
