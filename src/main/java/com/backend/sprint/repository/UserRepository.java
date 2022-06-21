package com.backend.sprint.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.UserDao;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserDao, Long>, JpaSpecificationExecutor<UserDao> {

	@Query(value = "SELECT * FROM users WHERE email = ?1 AND active is true", nativeQuery = true)
	UserDao findByEmail(String email);

	@Query(value = "SELECT * FROM users WHERE is_admin is true", nativeQuery = true)
	List<UserDao> findAdminUsers();

	@Query(value = "SELECT distinct(uss2.user_id) as user_id FROM users_sport_schools uss1 "
			+ "INNER JOIN users_sport_schools uss2 ON uss1.sport_school_id = uss2.sport_school_id "
			+ "WHERE uss1.user_id = ?1 ", nativeQuery = true)
	List<Long> findManagerUserUserIds(long id);
}
