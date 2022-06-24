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
@Table(name = "school_users_olympic")
@Data
@NoArgsConstructor
public class SchoolUsersOlympic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sou_id")
	public Integer souId;
	
	@Column(name = "status")
	public String status;
	
	@Column(name = "u_id")
	public Integer uId;
	
	@Column(name = "olympic")
	public String olympic;
	
	@Column(name = "creater")
	public String creater;

	@Column(name = "createday")
	public String createday;

	@Column(name = "modifyday")
	public String modifyday;
}
