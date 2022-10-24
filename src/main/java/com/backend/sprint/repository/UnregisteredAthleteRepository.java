package com.backend.sprint.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.UnregisteredAthleteDao;

@Repository
public interface UnregisteredAthleteRepository extends PagingAndSortingRepository<UnregisteredAthleteDao, Long>,
		JpaSpecificationExecutor<UnregisteredAthleteDao> {

	@Query(value = "SELECT * FROM unregistered_athletes WHERE last_unregister_date < ?1 AND active = true", nativeQuery = true)
	public List<UnregisteredAthleteDao> findByLastUnregisterDate(Date lastUnregisterDate);

}
