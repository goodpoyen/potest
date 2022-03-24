package com.olympic.mailParser.DAO.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sign_up_students")
@Data
@NoArgsConstructor
public class SignUpStudents {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "st_id")
	public Integer stId;

	@Column(name = "oly_id")
	public String olyId;

	@Column(name = "name")
	public String name;

	@Column(name = "olympic")
	public String olympic;

	@Column(name = "id_card")
	public String idCard;

	@Column(name = "school_name")
	public String schoolName;

	@Column(name = "grade")
	public String grade;

	@Column(name = "birthday")
	public String birthday;

	@Column(name = "email")
	public String email;

	@Column(name = "gender")
	public String gender;

	@Column(name = "creater")
	public String creater;

}
