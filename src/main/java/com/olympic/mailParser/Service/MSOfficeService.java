package com.olympic.mailParser.Service;

import java.io.File;

import org.json.JSONObject;

public interface MSOfficeService {

	JSONObject readExcelToCSV(String file, String fileType, String destDir, String pwd);
	
	JSONObject readExcel(String file, String fileType, String destDir, String pwd);
	
	void deleteFile(File file);
}
