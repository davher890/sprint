package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.TrainerDao;

public interface TrainerRepository
		extends PagingAndSortingRepository<TrainerDao, Long>, JpaSpecificationExecutor<TrainerDao> {

	@Query(value = "SELECT * FROM trainers WHERE name = ?1", nativeQuery = true)
	public TrainerDao findByName(String name);

	@Query(value = "SELECT * FROM trainers WHERE sport_school_id = ?1 ORDER BY name", nativeQuery = true)
	public List<TrainerDao> findBySportSchoolId(long id);

}
