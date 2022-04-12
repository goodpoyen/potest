package com.olympic.mailParser.Service;

import java.io.IOException;

import org.json.JSONObject;

public interface OpenOfficeService {
	JSONObject readODSToCSV(String fileName, String destDir, String pwd) throws IOException;
}
