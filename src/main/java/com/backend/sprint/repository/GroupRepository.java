package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.GroupDao;

public interface GroupRepository
		extends PagingAndSortingRepository<GroupDao, Long>, JpaSpecificationExecutor<GroupDao> {

	@Query(value = "SELECT * FROM groups WHERE name = ?1 ", nativeQuery = true)
	public GroupDao findByName(String name);

}
