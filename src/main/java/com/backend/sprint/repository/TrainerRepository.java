package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.TrainerDao;

public interface TrainerRepository
		extends PagingAndSortingRepository<TrainerDao, Long>, JpaSpecificationExecutor<TrainerDao> {

}
