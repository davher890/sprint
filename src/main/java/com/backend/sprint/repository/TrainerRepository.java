package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.TrainerDao;

public interface TrainerRepository
		extends PagingAndSortingRepository<TrainerDao, Long>, JpaSpecificationExecutor<TrainerDao> {

	@Query(value = "SELECT * FROM trainers WHERE name = ?1 ", nativeQuery = true)
	public TrainerDao findByName(String name);

}
