package com.olympic.mailParser.Service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface TOISignUpService {
	String save(JSONArray SingUpdata, String olyId, String createrEmail, int headerCount,JSONArray signupColumns);
	
	JSONObject checkSignUpData(JSONObject student, String value);
	
	JSONObject processSignUpData(JSONArray item, JSONArray signupColumns, String[] headerData);
	
	JSONObject prepareSaveData(JSONObject saveData, String olyId, String createrEmail);
}
