package com.olympic.mailParser.DAO.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.olympic.mailParser.DAO.Entity.SchoolUsers;

public interface SchoolUsersRepository extends JpaRepository<SchoolUsers, Long> {
	SchoolUsers findBySchoolNumberAndNameAndEmailAndTel(String schoolNumber, String name, String email, String tel);

	@Query(value = "select a.*, b.school_name, c.olympic, c.status\n" + "from school_users as a \n"
			+ "left join school_list as b on a.school_number = b.school_number\n"
			+ "left join school_users_olympic as c on a.u_id = c.u_id", nativeQuery = true)
	List<Map<String, Object>> getSchoolUsers();

	@Query(value = "select a.*, b.school_name, c.olympic, c.status\n" + "from school_users as a \n"
			+ "left join school_list as b on a.school_number = b.school_number \n"
			+ "left join school_users_olympic as c on a.u_id = c.u_id\n" + "where c.olympic = ?1", nativeQuery = true)
	List<Map<String, Object>> getSchoolUsersForOmlypic(String omlypic);

	@Query(value = "select a.school_number, b.olympic, b.status\n" + "from school_users as a\n"
			+ "left join school_users_olympic as b on a.u_id = b.u_id\n"
			+ "where a.school_number = ?1 and b.olympic = ?2 and (b.status = '1' or b.status = '3')", nativeQuery = true)
	List<Map<String, Object>> getOmlypicStatus(String schoolNumber, String omlypic);
}
