package com.olympic.mailParser.Service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.DAO.Entity.SchoolUsers;
import com.olympic.mailParser.DAO.Entity.SchoolUsersOlympic;
import com.olympic.mailParser.DAO.Repository.SchoolUsersOlympicRepository;
import com.olympic.mailParser.DAO.Repository.SchoolUsersRepository;
import com.olympic.mailParser.utils.Verify;

@Service
public class RegisterServiceImpl {
	@Autowired
	private Verify Verify;

	@Autowired
	private AES256ServiceImpl AES256ServiceImpl;

	@Autowired
	private SchoolUsersRepository SchoolUsersRepository;
	
	@Autowired
	private SchoolUsersOlympicRepository SchoolUsersOlympicRepository;

	private String errorMessage;

	private int registerCount = 0;

	private String[] olympicList = { "TMO", "IPHO", "TWICHO", "CTBO", "TOI", "IESO", "TWIJSO"};

	public String save(JSONArray registerData, String createrEmail, JSONArray registerColumns) {
		int count = 0;
		List<String> headerData = new ArrayList();
		AES256ServiceImpl.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");
		errorMessage = "";

		for (int index = 0; index < registerData.length(); index++) {

			JSONArray item = registerData.getJSONArray(index);

			if (index == 0) {
				for (int subIndex = 0; subIndex < item.length(); subIndex++) {
					if (!"".equals(item.get(subIndex).toString())) {
						headerData.add(subIndex, item.get(subIndex).toString());
					}
				}
				continue;
			}

			JSONObject result = processRegisterData(item, registerColumns, headerData);

			JSONObject saveData = result.getJSONObject("saveData");
			String teacherName = result.getString("teacherName");
			Boolean status = result.getBoolean("status");
			String error = result.getString("error");

			if (!status) {
				if (error.equals("header naming error")) {
					errorMessage = error;
					break;
				} else {
					if (errorMessage == null) {
						errorMessage = "第" + (index + 1) + "筆資料-" + teacherName + "-" + error;
					} else {
						errorMessage += "第" + (index + 1) + "筆資料-" + teacherName + "-" + error;
					}

					errorMessage += "\r\n";
				}
			}

			try {
				if (saveData.length() > 0) {
					saveData = prepareSaveData(saveData);

					for (String key : saveData.keySet()) {
						if (Arrays.asList(this.olympicList).contains(key) && saveData.getString(key).equals("1")) {
							if (checkOlympicExist(saveData.getString("schoolNumber"), key)) {
								insertRegisterData(saveData, key, createrEmail);
							} else {
								continue;
							}
						}
					}
				}
			} catch (Exception e) {
				return "檔案有問題";
			}
		}

		return errorMessage;
	}

	public Boolean checkOlympicExist(String schoolNumber, String olympic) {
		Boolean status = false;
		
		List<Map<String, Object>> olympicAccountData = SchoolUsersRepository.getOmlypicStatus(schoolNumber, olympic);

		if (olympicAccountData.size() > 0) {
			for (Map<String, Object> accountData : olympicAccountData) {
				if (accountData.get("status").equals("1")) {
					errorMessage += olympic + "奧林匹亞 該校已有人負責\r\n";
				} else if (accountData.get("status").equals("3")) {
					errorMessage += olympic + "奧林匹亞 已有負責人申請中\r\n";
				}
			}
		}else {
			status = true;
		}

		return status;
	}
	
	public void insertRegisterData (JSONObject saveData, String olympic, String createrEmail) {
		int uId = 0;

		SchoolUsers checkTeacher = SchoolUsersRepository.findBySchoolNumberAndNameAndEmailAndTel(
				saveData.getString("schoolNumber"), saveData.getString("name"), saveData.getString("email"),
				saveData.getString("tel"));

		if (checkTeacher != null) {
			uId = checkTeacher.getUId();
		} else {
			SchoolUsers teacher = new SchoolUsers();
			teacher.setSchoolNumber(saveData.getString("schoolNumber"));
			teacher.setName(saveData.getString("name"));
			teacher.setEmail(saveData.getString("email"));
			teacher.setTel(saveData.getString("tel"));
			teacher = SchoolUsersRepository.save(teacher);
			uId = teacher.getUId();
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String now = dtf.format(LocalDateTime.now()).toString();

		SchoolUsersOlympic usersOlympic = new SchoolUsersOlympic();
		
		usersOlympic.setUId(uId);
		usersOlympic.setStatus("3");
		usersOlympic.setOlympic(olympic);
		usersOlympic.setCreater(createrEmail);
		usersOlympic.setCreateday(now);
		usersOlympic.setModifyday(now);
		
		SchoolUsersOlympicRepository.save(usersOlympic);
	}

	public JSONObject processRegisterData(JSONArray item, JSONArray registerColumns, List<String> headerData) {
		JSONObject result = new JSONObject();
		JSONObject saveData = new JSONObject();
		String teacherName = "";
		Boolean status = true;
		String error = "";
		int checkCount = 0;

		for (int subIndex = 0; subIndex < item.length(); subIndex++) {
			for (int data = 0; data < registerColumns.length(); data++) {
				if (headerData.get(subIndex).equals(registerColumns.getJSONObject(data).getString("columnName"))) {
					checkCount++;
					JSONObject checkResult = checkRegisterData(registerColumns.getJSONObject(data),
							item.get(subIndex).toString());

					if ("".equals(teacherName)
							&& registerColumns.getJSONObject(data).getString("columnKey").equals("name")) {
						teacherName = item.get(subIndex).toString();
					}

					if (!checkResult.getBoolean("status") && status) {
						status = false;
					}

					if (status) {
						saveData.put(registerColumns.getJSONObject(data).getString("columnKey"),
								item.get(subIndex).toString());
					} else {
						saveData = new JSONObject();
					}

					error += checkResult.getString("error");

					break;
				}
			}
		}

		if (headerData.size() == checkCount) {
			if (this.registerCount < 1) {
				error += "未填寫負責奧匹;";
				saveData = new JSONObject();
			}

			result.put("status", status);
			result.put("saveData", saveData);
			result.put("error", error);
			result.put("teacherName", teacherName);
		} else {
			result.put("status", false);
			result.put("saveData", saveData);
			result.put("error", "header naming error");
			result.put("teacherName", teacherName);
		}

		return result;
	}

	public JSONObject checkRegisterData(JSONObject teacher, String value) {
		JSONObject result = new JSONObject();
		Boolean status = true;
		String error = "";

		if (!teacher.getBoolean("isNull") && "".equals(value)) {
			status = false;
			error += teacher.get("columnName") + "不能為空;";
		} else if (teacher.getString("columnKey").equals("TMO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦數奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("IPHO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦物奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("TWICHO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦化奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("CTBO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦生奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("TOI")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦資奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("IESO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦地奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
			}
		} else if (teacher.getString("columnKey").equals("TWIJSO")) {
			if (!Verify.checkValue(Integer.parseInt(value), 0, 1)) {
				status = false;
				error += "負責承辦國中科奧 請填寫0或1 (0表示不負責 1表示負責);";
			}

			if (Integer.parseInt(value) == 1) {
				this.registerCount++;
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

	public JSONObject prepareSaveData(JSONObject saveData) {
		saveData.put("uId", 0);
		saveData.put("email", AES256ServiceImpl.encode(saveData.getString("email")));
		saveData.put("tel", AES256ServiceImpl.encode(saveData.getString("tel")));

		SchoolUsers teacher = SchoolUsersRepository.findBySchoolNumberAndNameAndEmailAndTel(
				saveData.getString("schoolNumber"), saveData.getString("name"), saveData.getString("email"),
				saveData.getString("tel"));

		if (teacher != null) {
			saveData.put("uId", teacher.getUId());
		}

		return saveData;
	}
}
