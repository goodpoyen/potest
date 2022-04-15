package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.MSOfficeService;
import com.olympic.mailParser.utils.FilterString;

@Service
public class MSOfficeServiceImpl implements MSOfficeService {

	@Autowired
	private FilterString FilterString;

	public JSONObject readExcelToCSV(String file, String fileType, String destDir, String pwd) {
		JSONObject result = new JSONObject();
		try {
			Workbook wb = null;

			SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

			String newFile = "";

			if (fileType.equals("xls")) {
				POIFSFileSystem pfs = new POIFSFileSystem(new FileInputStream(destDir + file));
				Biff8EncryptionKey.setCurrentUserPassword(pwd);
				newFile = file.replace("xls", "csv");
				wb = WorkbookFactory.create(pfs);
			} else if (fileType.equals("xlsx")) {
				FileInputStream in = new FileInputStream(destDir + file);
				POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
				EncryptionInfo encInfo = new EncryptionInfo(poifsFileSystem);
				Decryptor decryptor = Decryptor.getInstance(encInfo);
				decryptor.verifyPassword(pwd);
				newFile = file.replace("xlsx", "csv");
				wb = WorkbookFactory.create(decryptor.getDataStream(poifsFileSystem));
			}

			Sheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getPhysicalNumberOfRows();
			String text = "";

			for (int r = 0; r < rowCount; r++) {
				Row row = sheet.getRow(r);
				int cellCount = row.getPhysicalNumberOfCells();

				for (int c = 0; c < cellCount; c++) {
					Cell cell = row.getCell(c);
					String cellType = cell.getCellType().toString();
					String cellValue = null;
					switch (cellType) {
					case "STRING":
						cellValue = cell.getStringCellValue();
						break;
					case "NUMERIC":
						if (DateUtil.isCellDateFormatted(cell)) {
							cellValue = fmt.format(cell.getDateCellValue());
						} else {
							int num = (int) cell.getNumericCellValue();
							cellValue = String.valueOf(num);
						}
						break;
					default:
						cellValue = "";
					}
					if (c + 1 < cellCount) {
						text += cellValue + ",";
					} else {
						text += cellValue + "\n";
					}
				}
			}

			text = FilterString.cleanXSS(text);
			text = FilterString.cleanSqlInjection(text);

			result.put("status", true);
			result.put("msg", "success");
			result.put("file", newFile);
			result.put("text", text);

			wb.close();
		} catch (Exception e) {
			result.put("status", false);
			if (e.getMessage().contains("java.security.Key.getEncoded")) {
				result.put("msg", "Wrong Password");
			} else if (e.getMessage().equals("Supplied password is invalid for salt/verifier/verifierHash")) {
				result.put("msg", "Wrong Password");
			} else {
				result.put("msg", "Something Wrong");
			}
			result.put("file", "");
			result.put("text", "");
		}

		deleteFile(new File(destDir + file));

		return result;
	}

	public JSONObject readExcel(String file, String fileType, String destDir, String pwd, int headerCount) {
		JSONObject result = new JSONObject();
		try {
			Workbook wb = null;

			SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

			List<List> dataList = new ArrayList();

			if (fileType.equals("xls")) {
				POIFSFileSystem pfs = new POIFSFileSystem(new FileInputStream(destDir + file));
				Biff8EncryptionKey.setCurrentUserPassword(pwd);
				wb = WorkbookFactory.create(pfs);
			} else if (fileType.equals("xlsx")) {
				FileInputStream in = new FileInputStream(destDir + file);
				POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
				EncryptionInfo encInfo = new EncryptionInfo(poifsFileSystem);
				Decryptor decryptor = Decryptor.getInstance(encInfo);
				decryptor.verifyPassword(pwd);
				wb = WorkbookFactory.create(decryptor.getDataStream(poifsFileSystem));
			}

			Sheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getPhysicalNumberOfRows();

			for (int r = 0; r < rowCount; r++) {
				Row row = sheet.getRow(r);
				List data = new ArrayList();

				for (int columnIndex = 0; columnIndex < headerCount; columnIndex ++) {
					String cellType = "";
					Cell cell = row.getCell(columnIndex);
					if (cell != null) {
						cellType = cell.getCellType().toString();
					} else {
						cellType = "NULL";
					}
					String cellValue = null;
					switch (cellType) {
					case "STRING":
						cellValue = cell.getStringCellValue();
						break;
					case "NUMERIC":
						if (DateUtil.isCellDateFormatted(cell)) {
							cellValue = fmt.format(cell.getDateCellValue());
						} else {
							int num = (int) cell.getNumericCellValue();
							cellValue = String.valueOf(num);
						}
						break;
					case "NULL":
						cellValue = "";
						break;
					default:
						cellValue = "";
					}

					cellValue = FilterString.cleanXSS(cellValue);
					cellValue = FilterString.cleanSqlInjection(cellValue);

					data.add(cellValue.trim());
				}
				
				int count = 0;
				
				for (int i = 0; i < data.size(); i ++) {
					if ("".equals(data.get(i))) {
						count++;
					}
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

			wb.close();
		} catch (Exception e) {
			result.put("status", false);
			if (e.getMessage().contains("java.security.Key.getEncoded")) {
				result.put("msg", "Wrong Password");
			} else if (e.getMessage().equals("Supplied password is invalid for salt/verifier/verifierHash")) {
				result.put("msg", "Wrong Password");
			} else {
				result.put("msg", "Something Wrong");
			}
			result.put("text", "");
		}

		deleteFile(new File(destDir + file));

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
