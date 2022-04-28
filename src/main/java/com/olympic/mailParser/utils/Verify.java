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

		if (dataValue.matches("[A-Z]\\d{4}")) {
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

	public Boolean checkDate(String date) {
		Boolean status = false;

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

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
}
