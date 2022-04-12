package com.olympic.mailParser.Service;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

public interface TikaReadFileService {
	JSONObject readExcelToCSV(String file, String destDir, String pwd) throws IOException;
	
	String setCSVString(String fileContent);
	
	void deleteFile(File file);
}
