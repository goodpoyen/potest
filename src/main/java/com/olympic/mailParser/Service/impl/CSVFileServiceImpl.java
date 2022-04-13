package com.olympic.mailParser.Service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.CSVFileService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class CSVFileServiceImpl implements CSVFileService{
	public Boolean createCSVFile(String newFile, String text, String destDir) throws IOException {
		BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File(destDir + newFile)), "UTF-8"));
		out.write('\ufeff');
		out.write(text);
		out.flush();
		out.close();
		
		return true;
	}
	
	public JSONObject readerCSV(String file, String destDir) {
		JSONObject result = new JSONObject();
		
		String filePath = destDir + file;

		FileInputStream fileInputStream;
		
		List<List> dataList = new ArrayList();
		
		try {
			fileInputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

			CSVReader csvReader = new CSVReader(reader);

			String[] nextRecord;
			int line = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				List data = new ArrayList();
				
				for (String cell : nextRecord) {
	                data.add(cell.trim());
	            }
				
				dataList.add(data);
				line++;
			}
			JSONArray text = new JSONArray(dataList);
			
			result.put("status", true);
			result.put("msg", "success");
			result.put("text", text);
		} catch (CsvValidationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("status", true);
			result.put("msg", e.getMessage());
			result.put("text", "");
		}
		
		return result;
	}

	public void deleteFile(File file) {
		if (file.exists()) {
			System.gc();
			file.delete();
		} else {
			System.out.println("該file路徑不存在！！");
		}
	}
}
