package com.backend.sprint.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.backend.sprint.model.dao.ScheduleDao;

public interface ScheduleRepository
		extends PagingAndSortingRepository<ScheduleDao, Long>, JpaSpecificationExecutor<ScheduleDao> {

	@Query(value = "SELECT * FROM schedules WHERE day = ?1 AND start_hour = ?2 AND start_minute = ?3 AND end_hour = ?4 AND end_minute = ?5 ", nativeQuery = true)
	public ScheduleDao findByTime(String day, int startHour, int startMinute, int endHour, int endMinute);

}
