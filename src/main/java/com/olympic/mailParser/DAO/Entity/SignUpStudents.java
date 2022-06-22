package com.olympic.mailParser.DAO.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

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

	@Column(name = "chinese_name")
	public String chineseName;

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

	@Column(name = "area")
	public String area;

	@Column(name = "english_name")
	public String englishName;

	@Column(name = "teacher")
	public String teacher;

	@Column(name = "remark")
	public String remark;

	@Column(name = "creater")
	public String creater;

	public SignUpStudents(JSONObject students) {
		this.olyId = "";
		this.chineseName = "";
		this.olympic = "";
		this.idCard = "";
		this.schoolName = "";
		this.grade = "";
		this.birthday = "";
		this.email = "";
		this.area = "";
		this.englishName = "";
		this.teacher = "";
		this.remark = "";
		this.creater = "";

		if (!students.isNull("stId")) {
			this.stId = students.getInt("stId");
		}

		if (!students.isNull("olyId")) {
			this.olyId = students.getString("olyId");
		}

		if (!students.isNull("chineseName")) {
			this.chineseName = students.getString("chineseName");
		}

		if (!students.isNull("olympic")) {
			this.olympic = students.getString("olympic");
		}

		if (!students.isNull("idCard")) {
			this.idCard = students.getString("idCard");
		}

		if (!students.isNull("schoolName")) {
			this.schoolName = students.getString("schoolName");
		}

		if (!students.isNull("grade")) {
			this.grade = students.getString("grade");
		}

		if (!students.isNull("birthday")) {
			this.birthday = students.getString("birthday");
		}

		if (!students.isNull("email")) {
			this.email = students.getString("email");
		}

		if (!students.isNull("area")) {
			this.area = students.getString("area");
		}

		if (!students.isNull("teacher")) {
			this.teacher = students.getString("teacher");
		}

		if (!students.isNull("remark")) {
			this.remark = students.getString("remark");
		}

		if (!students.isNull("creater")) {
			this.creater = students.getString("creater");
		}
	}

}
