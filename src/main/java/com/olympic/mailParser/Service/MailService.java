package com.olympic.mailParser.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import com.opencsv.exceptions.CsvValidationException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

public interface MailService {
	POP3Store mailConnectPOP3(String host, String mail, String pass) throws Exception;

	POP3Folder getPOP3Folder(POP3Store store) throws MessagingException;

	IMAPStore mailConnectIMAP(String host, String mail, String pass) throws Exception;

	IMAPFolder getIMAPFolder(IMAPStore store) throws MessagingException;

	String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException;

	String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException;

	String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException;

	String getSentDate(MimeMessage msg, String pattern) throws MessagingException;

	boolean isContainAttachment(Part part) throws MessagingException, IOException;

	boolean isSeen(MimeMessage msg) throws MessagingException;

	String getPriority(MimeMessage msg) throws MessagingException;

	void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException;

	void setMailRead(MimeMessage msg, Boolean flag) throws MessagingException;

	String saveAttachment(Part part, String destDir, String fileType) throws UnsupportedEncodingException,
			MessagingException, FileNotFoundException, IOException, CsvValidationException;

	String saveFile(InputStream is, String destDir, String fileName, String fileType)
			throws FileNotFoundException, IOException, CsvValidationException;

	String decodeText(String encodeText) throws UnsupportedEncodingException;

	void sendEmail(HashMap<String, String> smtp, HashMap<String, String> mail);

	Boolean checkFileType(Part part, String type) throws IOException, MessagingException;
	
	void mailMessages(MimeMessage msg) throws MessagingException, IOException;
}
