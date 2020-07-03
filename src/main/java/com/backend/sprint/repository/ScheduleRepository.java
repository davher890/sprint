package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.sprint.model.dao.ScheduleDao;

public interface ScheduleRepository extends JpaRepository<ScheduleDao, Long> {

}
