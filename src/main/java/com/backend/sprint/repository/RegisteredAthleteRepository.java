package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.RegisteredAthleteDao;

public interface RegisteredAthleteRepository
		extends PagingAndSortingRepository<RegisteredAthleteDao, Long>, JpaSpecificationExecutor<RegisteredAthleteDao> {

}
