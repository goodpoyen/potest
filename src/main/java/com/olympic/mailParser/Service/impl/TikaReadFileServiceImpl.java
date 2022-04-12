package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.olympic.mailParser.Service.TikaReadFileService;

@Service
public class TikaReadFileServiceImpl implements TikaReadFileService {
	public JSONObject readExcelToCSV(String file, String destDir, String pwd) throws IOException {
		JSONObject result = new JSONObject();

		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();

		FileInputStream inputstream = new FileInputStream(new File(destDir + file));

		ParseContext pcontext = new ParseContext();
		TesseractOCRConfig config = new TesseractOCRConfig();
		pcontext.set(TesseractOCRConfig.class, config);
		pcontext.set(Parser.class, parser);
		pcontext.set(PasswordProvider.class, new PasswordProvider() {
			public String getPassword(Metadata metadata) {
				return pwd;
			}
		});

		try {
			parser.parse(inputstream, handler, metadata, pcontext);

			String newFile = "";

			newFile = file.replace("xls", "csv");
			newFile = file.replace("xlsx", "csv");

			String text = setCSVString(handler.toString());

			result.put("status", true);
			result.put("msg", "success");
			result.put("file", newFile);
			result.put("text", text);
		} catch (IOException e) {
			result.put("status", false);
			result.put("msg", e.getMessage());
			result.put("file", "");
			result.put("text", "");
		} catch (SAXException e) {
			result.put("status", false);
			result.put("msg", e.getMessage());
			result.put("file", "");
			result.put("text", "");
		} catch (TikaException e) {
			result.put("status", false);
			if (e.getMessage().equals("Unable to process: document is encrypted")) {
				result.put("msg", "Wrong Password");
			} else {
				result.put("msg", "Something Wrong");
			}
			result.put("file", "");
			result.put("text", "");
		}

		inputstream.close();

		deleteFile(new File(destDir + file));

		return result;
	}

	public String setCSVString(String fileContent) {
		fileContent = fileContent.toString().replace("\t\t", ",");
		fileContent = fileContent.toString().replace("\t", ",");

		String[] record = fileContent.split("\n");

		String CSVtext = "";
		for (int index = 0; index < record.length; index++) {
			if (!record[index].contains(",")) {
				continue;
			}

			if (record[index].substring(0).contains(",")) {
				record[index] = record[index].substring(1, record[index].length());
			}

			if (record[index].length() <= 0) {
				continue;
			}

			CSVtext += record[index] + "\n";
		}

		return CSVtext;
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
