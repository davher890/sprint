package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.sprint.model.dao.GroupDao;

public interface GroupRepository extends JpaRepository<GroupDao, Long> {

	@Query(value = "SELECT * FROM groups WHERE name = ?1 ", nativeQuery = true)
	public GroupDao findByName(String name);

}
