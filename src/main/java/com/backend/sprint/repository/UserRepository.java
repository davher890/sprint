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
}
