package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.RegisteredAthleteDao;

@Repository
public interface RegisteredAthleteRepository
		extends PagingAndSortingRepository<RegisteredAthleteDao, Long>, JpaSpecificationExecutor<RegisteredAthleteDao> {

}
