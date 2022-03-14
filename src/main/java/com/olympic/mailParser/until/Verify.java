package com.olympic.mailParser.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class Verify {
	
	public Boolean checkLength (String dataValue, Integer length) {
		Boolean status = false;
		
		if (dataValue.length() <= length) {
			status = true;
		}
		
		return status;
	}
	
	public Boolean checkValue (Integer dataValue, Integer min, Integer max) {
		Boolean status = false;

		if (dataValue >= min && dataValue <= max) {
			status = true;
		}
		
		return status;
	}
	
	public Boolean checkIdCard (String dataValue) {
		Boolean status = false;

		dataValue = dataValue.toUpperCase();
		
		if (dataValue.matches("[A-Z]\\d{4}")) {
			status = true;
		}
		
		return status;
	}
	
	public Boolean checkEmail (String email) {
		Boolean status = false;
		
		String regx = "^[A-Za-z0-9+_.-]+@(.+)$";
		
		Pattern pattern = Pattern.compile(regx);
		
		Matcher matcher = pattern.matcher(email);
		
		if (matcher.matches()) {
			status = true;
		}
		
		return status;
	}
	
	public Boolean checkDate (String date) {
		Boolean status = false;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		
		try{
			format.setLenient(false);
			format.parse(date);
			status = true;
		}catch(ParseException e){		
			status = false;
		}
		
		return status;
	}
	
	public Boolean checkSubject (String olympic) {
		Boolean status = false;
		
		if (olympic.contains("[TOI]奧林匹亞")) {
			status = true;
		}
			
		return status;
	}
	
	public String checkOlympic (String olympic) {
		
		if (olympic.contains("[TOI]")) {
			return "TOI";
		}
			
		return "";
	}
}
