package com.olympic.mailParser.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;
import com.olympic.mailParser.DAO.Repository.SignUpStudentsRepository;
import com.olympic.mailParser.Service.TOISignUpService;
import com.olympic.mailParser.utils.Verify;

@Service
public class TOISignUpServiceImpl implements TOISignUpService {
	@Autowired
	private Verify Verify;

	@Autowired
	private AES256ServiceImpl AES256ServiceImpl;

	private String errorMessage;

	@Autowired
	private SignUpStudentsRepository signUpStudentsRepository;

	public String save(String[] SingUpdata, String olyId, String createrEmail, int index) {
		errorMessage = "";
		try {
			errorMessage = "";

			AES256ServiceImpl.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");

			SignUpStudents student = signUpStudentsRepository.findByNameAndIdCard(SingUpdata[1],
					AES256ServiceImpl.encode(SingUpdata[2]));

			if (student == null) {
				student = new SignUpStudents();
			}

			student.setOlympic(SingUpdata[0].toUpperCase());
			student.setName(SingUpdata[1]);
			student.setIdCard(SingUpdata[2]);
			student.setSchoolName(SingUpdata[3]);
			student.setGrade(SingUpdata[4]);
			student.setBirthday(SingUpdata[5]);
			student.setEmail(SingUpdata[6]);
			student.setGender(SingUpdata[7]);
			student.setCreater(createrEmail);
			student.setOlyId(olyId);

			if (checkSignUpDataIsNull(student, index)) {
				if (checkSignUpData(student, index)) {
					student.setIdCard(AES256ServiceImpl.encode(SingUpdata[2]));
					student.setBirthday(AES256ServiceImpl.encode(SingUpdata[5]));
					student.setEmail(AES256ServiceImpl.encode(SingUpdata[6]));
					signUpStudentsRepository.save(student);
				} else {
					errorMessage += "\r\n";
				}
			} else {
				errorMessage += "\r\n";
			}

			return errorMessage;
		} catch (Exception e) {
			return "檔案有問題";
		}

	}

	public Boolean checkSignUpData(SignUpStudents student, int index) {
		Boolean status = true;
		String error = "";

		if (!Verify.checkIdCard(student.getIdCard())) {
			status = false;
			error += "身分證有誤;";
		}

		if (!Verify.checkValue(Integer.parseInt(student.getGrade()), 7, 12)) {
			status = false;
			error += "年級有誤;";
		}

		if (!Verify.checkDate(student.getBirthday())) {
			status = false;
			error += "出生日期有誤;";
		}

		if (!Verify.checkEmail(student.getEmail())) {
			status = false;
			error += "信箱有誤;";
		}

//    	if (!Verify.checkValue(Integer.parseInt(student.getGender()), 1, 2)) {
//    		status = false;
//    		errorMessage += "性別有誤;";
//    	}

		if (!status) {
			if (errorMessage == null) {
				errorMessage = "第" + (index + 1) + "筆資料-" + student.getName() + "-" + error;
			} else {
				errorMessage += "第" + (index + 1) + "筆資料-" + student.getName() + "-" + error;
			}
		}

		return status;
	}

	public Boolean checkSignUpDataIsNull(SignUpStudents student, int index) {
		Boolean status = true;
		String error = "";

		student.getOlympic();

		if ("".equals(student.getOlympic())) {
			status = false;
			error += "類別不能為空;";
		}

		if ("".equals(student.getName())) {
			status = false;
			error += "姓名不能為空;";
		}

		if ("".equals(student.getIdCard())) {
			status = false;
			error += "身分證不能為空;";
		}

		if ("".equals(student.getSchoolName())) {
			status = false;
			error += "校名不能為空;";
		}

		if ("".equals(student.getGrade())) {
			status = false;
			error += "年級不能為空;";
		}

		if ("".equals(student.getBirthday())) {
			status = false;
			error += "生日不能為空;";
		}

		if ("".equals(student.getEmail())) {
			status = false;
			error += "信箱不能為空;";
		}

		if ("".equals(student.getGender())) {
			status = false;
			error += "性別不能為空;";
		}

		if (!status) {
			if (errorMessage == null) {
				errorMessage = "第" + (index + 1) + "筆資料-" + student.getName() + "-" + error;
			} else {
				errorMessage += "第" + (index + 1) + "筆資料-" + student.getName() + "-" + error;
			}
		}

		return status;
	}
}
