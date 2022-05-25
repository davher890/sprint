package com.backend.sprint.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.UserDao;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserDao, Long>, JpaSpecificationExecutor<UserDao> {

	Optional<UserDao> findByUsername(String username);

	Optional<UserDao> findByEmail(String email);

	@Query(value = "SELECT * FROM users WHERE is_admin is true", nativeQuery = true)
	List<UserDao> findAdminUsers();
}
