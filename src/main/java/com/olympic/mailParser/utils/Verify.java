package com.olympic.mailParser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
