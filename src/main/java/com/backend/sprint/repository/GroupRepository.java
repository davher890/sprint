package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.GroupDao;

public interface GroupRepository
		extends PagingAndSortingRepository<GroupDao, Long>, JpaSpecificationExecutor<GroupDao> {

	@Query(value = "SELECT * FROM groups WHERE name = ?1 AND active = true AND sport_school_id = ?2 ORDER BY name", nativeQuery = true)
	public GroupDao findByName(String name, int sportSchoolId);

	@Query(value = "SELECT * FROM groups WHERE trainer_id = ?1 AND active = true ORDER BY name", nativeQuery = true)
	public Set<GroupDao> findByTrainerId(long trainerId);

	@Query(value = "SELECT * FROM groups WHERE sport_school_id = ?1 AND active = true ORDER BY name", nativeQuery = true)
	public Set<GroupDao> findBySportSchoolId(long sportSchoolId);

	@Query(value = "SELECT * FROM groups WHERE sport_school_id = ?1 AND id IN (?2) AND active = true ORDER BY name", nativeQuery = true)
	public Set<GroupDao> findBySportSchoolIdAndGroupIds(long sportSchoolId, Set<Long> groupIds);

	@Query(value = "SELECT * FROM groups WHERE specialization = ?1 AND active = true ORDER BY name", nativeQuery = true)
	public Set<GroupDao> findBySpecialization(boolean specialization);

}
