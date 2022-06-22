package com.olympic.mailParser.DAO.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.olympic.mailParser.DAO.Entity.OlympicSchedule;

public interface OlympicScheduleRepository extends JpaRepository<OlympicSchedule, Long> {

	@Query(value = "select oly_id, signup_name, rules FROM olympic_schedule where signup_name = ?1 and start <= ?2 and end >= ?2", nativeQuery = true)
	List<Map<String, Object>> getOlympicSchedule(String signupName, String nowDate);
}
