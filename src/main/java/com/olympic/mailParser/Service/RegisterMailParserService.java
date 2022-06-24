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

public interface RegisterMailParserService {
	public POP3Store mailConnectPOP3() throws Exception;

	public POP3Folder getPOP3Folder(POP3Store store) throws Exception;

	public IMAPStore mailConnectIMAP() throws Exception;

	public IMAPFolder getIMAPFolder(IMAPStore store) throws Exception;

	public HashMap<String, String> getSmtp();

	public void parseMessagePOP3(POP3Store stroe, POP3Folder folder)
			throws MessagingException, IOException, CsvException;

	public void parseMessageIMAP(IMAPStore stroe, IMAPFolder folder)
			throws MessagingException, IOException, CsvException;

	public JSONObject getFileType(MimeMessage msg) throws JSONException, IOException, MessagingException;

	public JSONObject readAttachment(String fileType, String newFile, int headerCount) throws IOException;
	
	

}
