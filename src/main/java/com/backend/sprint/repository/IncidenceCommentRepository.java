package com.backend.sprint.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.IncidenceCommentDao;

@Repository
public interface IncidenceCommentRepository
		extends PagingAndSortingRepository<IncidenceCommentDao, Long>, JpaSpecificationExecutor<IncidenceCommentDao> {

	@Query(value = "SELECT * FROM incidence_comments WHERE incidence_id = ?1 ORDER BY created_at DESC ", nativeQuery = true)
	public Set<IncidenceCommentDao> findIncidenceComments(long incidenceId);
}
