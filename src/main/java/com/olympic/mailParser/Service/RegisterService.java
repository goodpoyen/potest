package com.olympic.mailParser.Service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public interface RegisterService {
	public String save(JSONArray registerData, String createrEmail, JSONArray registerColumns);

	public Boolean checkOlympicExist(String schoolNumber, String olympic);

	public void insertRegisterData(JSONObject saveData, String olympic, String createrEmail);

	public JSONObject processRegisterData(JSONArray item, JSONArray registerColumns, List<String> headerData);

	public JSONObject checkRegisterData(JSONObject teacher, String value);

	public JSONObject prepareSaveData(JSONObject saveData);
}
