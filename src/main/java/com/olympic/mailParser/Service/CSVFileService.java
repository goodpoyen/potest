package com.olympic.mailParser.Service;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

public interface CSVFileService {
	Boolean createCSVFile(String newFile, String text, String destDir) throws IOException;
	
	JSONObject readerCSV(String file, String destDir);
	
	void deleteFile(File file);
}
