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
@Table(name = "olympic_schedule")
@Data
@NoArgsConstructor
public class OlympicSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "oly_id")
	public Integer olyId;

	@Column(name = "olympic")
	public String olympic;

	@Column(name = "signup_name")
	public String signupName;

	@Column(name = "start")
	public String start;

	@Column(name = "end")
	public String end;
}
