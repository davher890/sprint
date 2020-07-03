package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.sprint.model.dao.GroupDao;

public interface GroupRepository extends JpaRepository<GroupDao, Long> {

}
