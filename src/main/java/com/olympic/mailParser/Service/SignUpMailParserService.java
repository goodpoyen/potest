package com.olympic.mailParser.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.exceptions.CsvException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

public interface SignUpMailParserService {
	POP3Store mailConnectPOP3() throws Exception;

	POP3Folder getPOP3Folder(POP3Store store) throws Exception;

	IMAPStore mailConnectIMAP() throws Exception;

	IMAPFolder getIMAPFolder(IMAPStore store) throws Exception;

	HashMap<String, String> getSmtp();

	void parseMessagePOP3(POP3Store stroe, POP3Folder folder) throws MessagingException, IOException, CsvException;

	void parseMessageIMAP(IMAPStore stroe, IMAPFolder folder) throws MessagingException, IOException, CsvException;

	JSONObject getOlympicScheduleData(String subject);

	String switchOlympic(JSONArray SingUpdata, MimeMessage msg, JSONArray signupColumns, String olyId)
			throws UnsupportedEncodingException, MessagingException;

	JSONObject getFileType(MimeMessage msg) throws JSONException, IOException, MessagingException;

	JSONObject readAttachment(String fileType, String newFile, int headerCount, String password) throws IOException;
}
