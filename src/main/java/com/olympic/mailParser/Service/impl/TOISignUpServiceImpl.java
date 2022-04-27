package com.olympic.mailParser.Service.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
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

	private int BATCH_SIZE = 500;

	public String save(JSONArray SingUpdata, String olyId, String createrEmail, int headerCount,
			JSONArray signupColumns) {

		int count = 0;
		String[] headerData = new String[headerCount];
		List<SignUpStudents> students = new ArrayList<>();
		AES256ServiceImpl.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");

		for (int index = 0; index < SingUpdata.length(); index++) {

			JSONArray item = SingUpdata.getJSONArray(index);

			if (index == 0) {
				for (int subIndex = 0; subIndex < item.length(); subIndex++) {
					headerData[subIndex] = item.get(subIndex).toString();
				}
				continue;
			}

			JSONObject saveData = new JSONObject();
			String studentName = "";
			Boolean status = true;
			String error = "";

			for (int subIndex = 0; subIndex < item.length(); subIndex++) {
				for (int data = 0; data < signupColumns.length(); data++) {
					if (headerData[subIndex].equals(signupColumns.getJSONObject(data).getString("columnName"))) {
						JSONObject checkResult = checkSignUpData(signupColumns.getJSONObject(data),
								item.get(subIndex).toString());

						if ("".equals(studentName)
								&& signupColumns.getJSONObject(data).getString("columnKey").equals("chineseName")) {
							studentName = item.get(subIndex).toString();
						}

						if (!checkResult.getBoolean("status") && status) {
							status = false;
						}

						if (status) {
							saveData.put(signupColumns.getJSONObject(data).getString("columnKey"),
									item.get(subIndex).toString());
						} else {
							saveData = new JSONObject();
						}

						error += checkResult.getString("error");
					}
				}
			}

			if (!status) {
				if (errorMessage == null) {
					errorMessage = "第" + (index + 1) + "筆資料-" + studentName + "-" + error;
				} else {
					errorMessage += "第" + (index + 1) + "筆資料-" + studentName + "-" + error;
				}

				errorMessage += "\r\n";
			}
			
			try {
				if (saveData.length() > 0) {
					saveData = prepareSaveData(saveData, olyId, createrEmail);
	
					students.add(new SignUpStudents(saveData));
	
					if (index + 1 == SingUpdata.length()) {
						signUpStudentsRepository.saveAll((Iterable<SignUpStudents>) students);
					} else {
						if (count <= BATCH_SIZE) {
							count++;
						} else {
							signUpStudentsRepository.saveAll((Iterable<SignUpStudents>) students);
							students = new ArrayList<>();
							count = 0;
						}
					}
				}
			} catch (Exception e) {
				return "檔案有問題";
			}
		}
//		System.out.println(errorMessage);
		return errorMessage;
	}

	public JSONObject checkSignUpData(JSONObject student, String value) {
		JSONObject result = new JSONObject();
		Boolean status = true;
		String error = "";

		if (!student.getBoolean("isNull") && "".equals(value)) {
			status = false;
			error += student.get("columnName") + "不能為空;";
		} else if (student.getString("columnKey").equals("idCard")) {
			if (!Verify.checkIdCard(value)) {
				status = false;
				error += "身分證有誤;";
			}
		} else if (student.getString("columnKey").equals("grade")) {
			if (!Verify.checkValue(Integer.parseInt(value), 7, 12)) {
				status = false;
				error += "年級有誤;";
			}
		} else if (student.getString("columnKey").equals("birthday")) {
			if (!Verify.checkDate(value)) {
				status = false;
				error += "出生日期有誤;";
			}
		} else if (student.getString("columnKey").equals("email")) {
			if (!Verify.checkEmail(value)) {
				status = false;
				error += "信箱有誤;";
			}
		}

		if (!status) {
			result.put("status", false);
			result.put("error", error);
		} else {
			result.put("status", true);
			result.put("error", "");
		}

		return result;
	}

	public JSONObject prepareSaveData(JSONObject saveData, String olyId, String createrEmail) {
		saveData.put("idCard", AES256ServiceImpl.encode(saveData.getString("idCard")));
		saveData.put("birthday", AES256ServiceImpl.encode(saveData.getString("birthday")));
		saveData.put("email", AES256ServiceImpl.encode(saveData.getString("email")));

		saveData.put("olympic", saveData.getString("olympic").toUpperCase());
		saveData.put("createrEmail", createrEmail);
		saveData.put("olyId", olyId);

		SignUpStudents student = signUpStudentsRepository.findByChineseNameAndIdCard(saveData.getString("chineseName"),
				saveData.getString("idCard"));
		
		if (student != null) {
			saveData.put("stId", student.getStId());
		}

		return saveData;
	}
}
