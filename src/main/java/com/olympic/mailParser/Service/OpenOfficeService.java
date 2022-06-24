package com.olympic.mailParser.Service;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

public interface OpenOfficeService {
	JSONObject readODSToCSV(String file, String destDir, String pwd) throws IOException;

	JSONObject readODS(String file, String destDir, String pwd, int headerCount) throws IOException;

	public JSONObject readODS(String file, String destDir, int headerCount) throws IOException;

	void deleteFile(File file);
}
