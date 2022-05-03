package com.olympic.mailParser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class Verify {

	public Boolean checkLength(String dataValue, Integer length) {
		Boolean status = false;

		if (dataValue.length() <= length) {
			status = true;
		}

		return status;
	}

	public Boolean checkValue(Integer dataValue, Integer min, Integer max) {
		Boolean status = false;

		if (dataValue >= min && dataValue <= max) {
			status = true;
		}

		return status;
	}

	public Boolean checkIdCard(String dataValue) {
		Boolean status = false;

		dataValue = dataValue.toUpperCase();

		if (dataValue.matches("[a-zA-Z][1-2]\\d{3}")) {
			status = true;
		}

		return status;
	}

	public Boolean checkEmail(String email) {
		Boolean status = false;

		String regx = "^[A-Za-z0-9+_.-]+@(.+)$";

		Pattern pattern = Pattern.compile(regx);

		Matcher matcher = pattern.matcher(email);

		if (matcher.matches()) {
			status = true;
		}

		return status;
	}

	public Boolean checkDate(String date, String dateFormat) {
		Boolean status = false;

		String[] buff = date.split("/");

		if (buff.length > 2) {
			return false;
		}

		SimpleDateFormat format = new SimpleDateFormat(dateFormat);

		try {
			format.setLenient(false);
			format.parse(date);
			status = true;
		} catch (ParseException e) {
			status = false;
		}

		return status;
	}
	
	public Boolean checkPassword(String password) {
		Boolean status = false;

		String passwordPattern = "^(?![0-9A-Za-z]+$)(?![\\W]+$)(?![A-Za-z\\W]+$)(?![0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)(?![a-z0-9\\W]+$)(?![a-zA-A\\W]+$)(?![0-9\\W]+$)[0-9A-Za-z\\W]{8,16}$";

		if (password.matches(passwordPattern)) {
			status = true;
		}

		return status;
	}

	public Boolean checkSubject(String olympic) {
		Boolean status = false;

		if (olympic.contains("[TMO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[IPHO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[TWICHO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[CTBO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[IESO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[TWIJSO]奧林匹亞")) {
			status = true;
		}

		if (olympic.contains("[TOI]奧林匹亞")) {
			status = true;
		}

		return status;
	}

	public String checkOlympic(String olympic) {
		if (olympic.contains("[TMO]")) {
			return "TMO";
		}

		if (olympic.contains("[IPHO]")) {
			return "IPHO";
		}

		if (olympic.contains("[TWICHO]")) {
			return "TWICHO";
		}

		if (olympic.contains("[CTBO]")) {
			return "CTBO";
		}

		if (olympic.contains("[IESO]")) {
			return "IESO";
		}

		if (olympic.contains("[TWIJSO]")) {
			return "TWIJSO";
		}

		if (olympic.contains("[TOI]")) {
			return "TOI";
		}

		return "";
	}

	public Boolean checkMedical(String dataValue) {
		Boolean status = false;

		if (dataValue.contains("症")) {
			status = false;
		} else if (dataValue.contains("炎")) {
			status = false;
		} else if (dataValue.contains("病")) {
			status = false;
		} else if (dataValue.contains("傷")) {
			status = false;
		} else if (dataValue.contains("鬱")) {
			status = false;
		}

//		else if (dataValue.contains("肝")) {
//			status = false;
//		}
//		else if (dataValue.contains("膽")) {
//			status = false;
//		}
//		else if (dataValue.contains("脾")) {
//			status = false;
//		}
//		else if (dataValue.contains("胃")) {
//			status = false;
//		}
//		else if (dataValue.contains("腎")) {
//			status = false;
//		}
//		else if (dataValue.contains("腦")) {
//			status = false;
//		}

//		else if (dataValue.contains("殘")) {
//			status = false;
//		}
//		else if (dataValue.contains("障礙")) {
//			status = false;
//		}
//		else if (dataValue.contains("身障")) {
//			status = false;
//		}

//		else if (dataValue.contains("失調")) {
//			status = false;
//		}
//		else if (dataValue.contains("過動")) {
//			status = false;
//		}
//		else if (dataValue.contains("癲癇")) {
//			status = false;
//		}
//		else if (dataValue.contains("妥瑞")) {
//			status = false;
//		}
//		else if (dataValue.contains("自閉")) {
//			status = false;
//		}
//		else if (dataValue.contains("亞斯")) {
//			status = false;
//		}
//		else if (dataValue.contains("ADHD")) {
//			status = false;
//		}
//		else if (dataValue.contains("adhd")) {
//			status = false;
//		}

		else {
			status = true;
		}

		return status;
	}
	
	public JSONArray getColumnSet() {
		JSONArray text = new JSONArray();

		JSONObject result = new JSONObject();

		result.put("columnKey", "olympic");
		result.put("columnName", "類別");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "chineseName");
		result.put("columnName", "中文姓名");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "idCard");
		result.put("columnName", "身分證");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "schoolName");
		result.put("columnName", "校名");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "grade");
		result.put("columnName", "年級");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "birthday");
		result.put("columnName", "生日");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "email");
		result.put("columnName", "信箱");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "area");
		result.put("columnName", "初選考區");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "englishName");
		result.put("columnName", "英文姓名");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "teacher");
		result.put("columnName", "初選指導老師");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "remark");
		result.put("columnName", "重要備註");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		return text;
	}
}
