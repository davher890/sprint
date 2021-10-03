package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.GroupDao;

public interface GroupRepository
		extends PagingAndSortingRepository<GroupDao, Long>, JpaSpecificationExecutor<GroupDao> {

	@Query(value = "SELECT * FROM groups WHERE name = ?1 AND active = true AND sport_school_id = ?2", nativeQuery = true)
	public GroupDao findByName(String name, int sportSchoolId);

	@Query(value = "SELECT * FROM groups WHERE trainer_id = ?1 AND active = true", nativeQuery = true)
	public Set<GroupDao> findByTrainerId(long trainerId);

	@Query(value = "SELECT * FROM groups WHERE specialization = ?1 AND active = true ", nativeQuery = true)
	public Set<GroupDao> findBySpecialization(boolean specialization);
}
