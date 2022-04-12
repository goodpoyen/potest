package com.olympic.mailParser.Service;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public interface ZipFileService {
	Boolean zipFile(String fileName, String destDir, String pwd) throws ZipException;

	JSONObject unZipFile(String fileName, String destDir, String pwd) throws IOException;

	String getFileNameFromExtraData(FileHeader fileHeader);

	void deleteFile(File file);
}
