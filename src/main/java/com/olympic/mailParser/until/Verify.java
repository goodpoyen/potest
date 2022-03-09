package com.olympic.mailParser.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		
		String format = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		
		if (email.matches(format)){ 
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
}
