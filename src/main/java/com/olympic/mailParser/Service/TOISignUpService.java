package com.olympic.mailParser.Service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public interface TOISignUpService {

	String save(JSONArray SingUpdata, String olyId, String createrEmail, JSONArray signupColumns);

	JSONObject checkSignUpData(JSONObject student, String value);

	JSONObject processSignUpData(JSONArray item, JSONArray signupColumns, List<String> headerData);

	JSONObject prepareSaveData(JSONObject saveData, String olyId, String createrEmail);
}
