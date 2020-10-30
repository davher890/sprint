package com.backend.sprint.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.UserDao;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserDao, Long>, JpaSpecificationExecutor<UserDao> {

	Optional<UserDao> findByUsername(String username);
}
