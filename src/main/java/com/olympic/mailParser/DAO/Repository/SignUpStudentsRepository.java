package com.olympic.mailParser.DAO.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;

public interface SignUpStudentsRepository extends JpaRepository<SignUpStudents, Long> {

	SignUpStudents findByNameAndIdCard(String name, String idCard);
}
