package com.backend.sprint.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.backend.sprint.model.dao.AthleteGroupScheduleDao;

@Repository
public interface AthleteGroupScheduleRepository extends PagingAndSortingRepository<AthleteGroupScheduleDao, Long>,
		JpaSpecificationExecutor<AthleteGroupScheduleDao> {

	@Query(value = "SELECT * FROM athlete_group_schedules WHERE athlete_id = ?1 ", nativeQuery = true)
	public Set<AthleteGroupScheduleDao> findByAthlete(long athleteId);

	@Query(value = "SELECT * FROM athlete_group_schedules WHERE group_id = ?1 ", nativeQuery = true)
	public Set<AthleteGroupScheduleDao> findByGroup(long groupId);

	@Query(value = "SELECT * FROM athlete_group_schedules WHERE schedule_id = ?1 ", nativeQuery = true)
	public Set<AthleteGroupScheduleDao> findBySchedule(long scheduleId);

	@Query(value = "SELECT * FROM athlete_group_schedules WHERE group_id = ?1 AND schedule_id IN (?2) ", nativeQuery = true)
	public Set<AthleteGroupScheduleDao> findByGroupAndSchedules(long groupId, List<Integer> scheduleIds);

	@Modifying
	@Query(value = "DELETE from athlete_group_schedules WHERE athlete_id = ?1", nativeQuery = true)
	public void deleteByAthlete(long athleteId);

}
