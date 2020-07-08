package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.sprint.model.dao.TrainerDao;

public interface TrainerRepository extends JpaRepository<TrainerDao, Long> {

}
