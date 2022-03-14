package com.olympic.mailParser.Service;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;

public interface TOISignUpService {
	String save(String[] SingUpdata);
	
	Boolean checkSignUpData(SignUpStudents student);
}
