package com.olympic.mailParser.Service;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;

public interface TOISignUpService {
	String save(String[] SingUpdata, String olyId, String createrEmail, int index);

	Boolean checkSignUpData(SignUpStudents student, int index);
	
	Boolean checkSignUpDataIsNull(SignUpStudents student, int index);
}
