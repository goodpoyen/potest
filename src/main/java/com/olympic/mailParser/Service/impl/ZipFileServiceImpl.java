package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.ZipFileService;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

@Service
public class ZipFileServiceImpl implements ZipFileService {
	public Boolean zipFile(String fileName, String destDir, String pwd) throws ZipException {
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setEncryptFiles(true);
		zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
		zipParameters.setEncryptionMethod(EncryptionMethod.AES);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		ZipFile zipFile = new ZipFile(destDir + dtf.format(LocalDateTime.now()) + ".zip");

		zipFile.setPassword(pwd.toCharArray());

		zipFile.addFile(new File(destDir + fileName), zipParameters);

		return true;
	}

	public JSONObject unZipFile(String fileName, String destDir, String pwd) throws IOException {
		JSONObject result = new JSONObject();

		try {
			ZipFile zipFile = new ZipFile(destDir + fileName);

			if (zipFile.isEncrypted()) {
				zipFile.setPassword(pwd.toCharArray());
			} else {
				result.put("status", false);
				result.put("msg", "Wrong Password");
				result.put("resultData", new JSONObject());
			}

			zipFile.getFileHeaders().forEach(v -> {
				String extractedFile = getFileNameFromExtraData(v);
				try {
					ArrayList<String> fileList = new ArrayList<String>();

					zipFile.extractFile(v, destDir, extractedFile);

					fileList.add(extractedFile);

					result.put("status", true);
					result.put("msg", "success");

					JSONArray reultData = new JSONArray(fileList);

					result.put("resultData", reultData);

				} catch (ZipException e) {
					result.put("status", false);
					result.put("resultData", new JSONObject());
					if (e.getMessage().equals("Wrong Password")) {
						result.put("msg", "Wrong Password");
					} else {
						result.put("msg", "Something Wrong");
					}
				}
			});

			deleteFile(new File(destDir + fileName));
			return result;
		} catch (IOException e) {
			result.put("status", false);
			result.put("msg", "Something Wrong");
			result.put("resultData", new JSONObject());
			deleteFile(new File(destDir + fileName));
			return result;
		}
	}

	public String getFileNameFromExtraData(FileHeader fileHeader) {
		if (fileHeader.getExtraDataRecords() != null) {
			for (ExtraDataRecord extraDataRecord : fileHeader.getExtraDataRecords()) {
				long identifier = extraDataRecord.getHeader();
				if (identifier == 0x7075) {
					byte[] bytes = extraDataRecord.getData();
					ByteBuffer buffer = ByteBuffer.wrap(bytes);
					byte version = buffer.get();
					assert (version == 1);
					int crc32 = buffer.getInt();
					return new String(bytes, 5, buffer.remaining(), StandardCharsets.UTF_8);
				}
			}
		}
		return fileHeader.getFileName();
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
