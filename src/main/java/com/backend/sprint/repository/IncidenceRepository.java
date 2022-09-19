package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.IncidenceDao;

@Repository
public interface IncidenceRepository
		extends PagingAndSortingRepository<IncidenceDao, Long>, JpaSpecificationExecutor<IncidenceDao> {

}
