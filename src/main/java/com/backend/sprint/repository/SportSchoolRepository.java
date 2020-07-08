package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.sprint.model.dao.SportSchoolDao;

public interface SportSchoolRepository extends JpaRepository<SportSchoolDao, Long> {

	@Query(value = "SELECT * FROM sport_schools WHERE name = ?1 ", nativeQuery = true)
	SportSchoolDao findByName(String name);

}
