package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.sprint.model.dao.AthleteDao;

public interface AthleteRepository extends JpaRepository<AthleteDao, Long> {

}
