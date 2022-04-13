package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.IOException;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.OpenOfficeService;
import com.olympic.mailParser.utils.FilterString;

@Service
public class OpenOfficeServiceImpl implements OpenOfficeService {
	@Autowired
	private ZipFileServiceImpl ZipFileServiceImpl;
	
	@Autowired
	private FilterString FilterString;

	public JSONObject readODSToCSV(String fileName, String destDir, String pwd) throws IOException {
		JSONObject result = new JSONObject();

		JSONObject unzipResult = ZipFileServiceImpl.unZipFile(fileName, destDir, pwd);

		if (unzipResult.getBoolean("status")) {

			int listSize = unzipResult.getJSONArray("resultData").length();

			String newFile = unzipResult.getJSONArray("resultData").get(listSize - 1).toString();

			if (!newFile.contains("ods")) {
				result.put("status", false);
				result.put("msg", "not ods file");
				result.put("file", "");
				result.put("text", "");

				return result;
			}
			
			try {
				SpreadSheet spreadsheet = SpreadSheet.createFromFile(new File(destDir + newFile));
	
				int nColCount = spreadsheet.getSheet(0).getColumnCount();
				int nRowCount = spreadsheet.getSheet(0).getRowCount();
	
				String text = "";
	
				for (int nRowIndex = 0; nRowIndex < nRowCount; nRowIndex++) {
					for (int nColIndex = 0; nColIndex < nColCount; nColIndex++) {
						String value = spreadsheet.getSheet(0).getCellAt(nColIndex, nRowIndex).getTextValue();
						if (nColIndex + 1 < nColCount) {
							text += value + ",";
						} else {
							text += value + "\n";
						}
					}
				}
				
				text = FilterString.cleanXSS(text);
				text = FilterString.cleanSqlInjection(text);
				
				result.put("status", true);
				result.put("msg", "success");
				result.put("file", newFile);
				result.put("text", text);
				
			} catch (IOException e) {
				result.put("status", false);
				result.put("msg", e.getMessage());
				result.put("file", "");
				result.put("text", "");
			}
		} else {
			result.put("status", false);
			result.put("msg", unzipResult.get("msg"));
			result.put("file", "");
			result.put("text", "");
		}

		return result;
	}
}
