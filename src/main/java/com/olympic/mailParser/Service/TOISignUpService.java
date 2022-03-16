package com.olympic.mailParser.Service;

import javax.mail.internet.MimeMessage;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;

public interface TOISignUpService {
	String save(String[] SingUpdata, String olyId, MimeMessage msg);
	
	Boolean checkSignUpData(SignUpStudents student);
}
