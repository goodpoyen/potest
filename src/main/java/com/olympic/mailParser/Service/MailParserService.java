package com.olympic.mailParser.Service;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.exceptions.CsvException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

public interface MailParserService {
	POP3Store mailConnectPOP3() throws Exception;

	POP3Folder getPOP3Folder(POP3Store store) throws Exception;

	IMAPStore mailConnectIMAP() throws Exception;

	IMAPFolder getIMAPFolder(IMAPStore store) throws Exception;

	HashMap<String, String> getSmtp();

	void parseMessagePOP3(POP3Store stroe, POP3Folder folder) throws MessagingException, IOException, CsvException;

	void parseMessageIMAP(IMAPStore stroe, IMAPFolder folder) throws MessagingException, IOException, CsvException;

	void saveSingUpData(String[] SingUpdata, MimeMessage msg);

	String switchOlympic(String[] SingUpdata, MimeMessage msg);

	JSONObject getFileType(MimeMessage msg) throws JSONException, IOException, MessagingException;
}
