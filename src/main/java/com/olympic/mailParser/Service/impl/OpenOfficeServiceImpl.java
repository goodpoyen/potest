package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.json.JSONArray;
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

	public JSONObject readODSToCSV(String file, String destDir, String pwd) throws IOException {
		JSONObject result = new JSONObject();

		JSONObject unzipResult = ZipFileServiceImpl.unZipFile(file, destDir, pwd);

		if (unzipResult.getBoolean("status")) {

			int listSize = unzipResult.getJSONArray("resultData").length();

			String newFile = unzipResult.getJSONArray("resultData").get(listSize - 1).toString();

			if (!newFile.contains("ods")) {
				result.put("status", false);
				result.put("msg", "not ods file");
				result.put("file", "");
				result.put("text", "");

				deleteFile(new File(destDir + newFile));

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

				String CSVFile = newFile.replace("ods", "csv");

				text = FilterString.cleanXSS(text);
				text = FilterString.cleanSqlInjection(text);

				result.put("status", true);
				result.put("msg", "success");
				result.put("file", CSVFile);
				result.put("text", text);

			} catch (IOException e) {
				result.put("status", false);
				result.put("msg", e.getMessage());
				result.put("file", "");
				result.put("text", "");
			}

			deleteFile(new File(destDir + newFile));
		} else {
			result.put("status", false);
			result.put("msg", unzipResult.get("msg"));
			result.put("file", "");
			result.put("text", "");
		}

		return result;
	}

	public JSONObject readODS(String file, String destDir, String pwd, int headerCount) throws IOException {
		JSONObject result = new JSONObject();

		JSONObject unzipResult = ZipFileServiceImpl.unZipFile(file, destDir, pwd);

		List<List> dataList = new ArrayList();

		if (unzipResult.getBoolean("status")) {

			int listSize = unzipResult.getJSONArray("resultData").length();

			String newFile = unzipResult.getJSONArray("resultData").get(listSize - 1).toString();

			if (!newFile.contains("ods")) {
				result.put("status", false);
				result.put("msg", "not ods file");
				result.put("file", "");
				result.put("text", "");

				deleteFile(new File(destDir + newFile));

				return result;
			}

			try {
				SpreadSheet spreadsheet = SpreadSheet.createFromFile(new File(destDir + newFile));
				int nColCount = spreadsheet.getSheet(0).getColumnCount();
				int nRowCount = spreadsheet.getSheet(0).getRowCount();
				
				if (nRowCount >= 100000) {
					result.put("status", false);
					result.put("msg", "over row data");
					result.put("text", "");

					deleteFile(new File(destDir + newFile));
					
					return result;
				}
				
				for (int nRowIndex = 0; nRowIndex < nRowCount; nRowIndex++) {
					List data = new ArrayList();
					int count = 0;
					for (int nColIndex = 0; nColIndex < nColCount; nColIndex++) {
						String value = spreadsheet.getSheet(0).getCellAt(nColIndex, nRowIndex).getTextValue();
						value = FilterString.cleanXSS(value);
						value = FilterString.cleanSqlInjection(value);

						if (nRowIndex == 0) {
							if (!"".equals(value.trim())) {
								data.add(value.trim());
							}
						} else {
							if ("".equals(value.trim())) {
								count++;
							}
							if (nColIndex < headerCount) {
								data.add(value.trim());
							}
						}
					}
			
					if (nRowIndex == 0 && data.size() != headerCount) {
						result.put("status", false);
						result.put("msg", "header count error");
						result.put("text", "");

						deleteFile(new File(destDir + newFile));

						return result;
					}

					if (count == headerCount) {
						continue;
					}

					dataList.add(data);
				}

				JSONArray text = new JSONArray(dataList);

				result.put("status", true);
				result.put("msg", "success");
				result.put("text", text);

			} catch (IOException e) {
				result.put("status", false);
				result.put("msg", e.getMessage());
				result.put("text", "");
			}

			deleteFile(new File(destDir + newFile));
		} else {
			result.put("status", false);
			result.put("msg", unzipResult.get("msg"));
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
