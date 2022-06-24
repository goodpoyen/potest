package com.olympic.mailParser.Service.impl;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.RegisterMailParserService;
import com.olympic.mailParser.utils.Verify;
import com.opencsv.exceptions.CsvException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

@Service
public class RegisterMailParserServiceImpl implements RegisterMailParserService {
	@Value("${mailFilePath}")
	private String mailFilePath;

	@Autowired
	private MailServiceImpl MailServiceImpl;

	@Autowired
	private Verify Verify;

	@Autowired
	private RegisterServiceImpl RegisterServiceImpl;

	@Autowired
	private OpenOfficeServiceImpl OpenOfficeServiceImpl;

	@Autowired
	private MSOfficeServiceImpl MSOfficeServiceImpl;

	private String errorMessage;

	public POP3Store mailConnectPOP3() throws Exception {
		return MailServiceImpl.mailConnectPOP3("mail.csie.ntnu.edu.tw", "tor@csie.ntnu.edu.tw", "tor@CSIE@6690");
	}

	public POP3Folder getPOP3Folder(POP3Store store) throws Exception {
		return MailServiceImpl.getPOP3Folder(store);
	}

	public IMAPStore mailConnectIMAP() throws Exception {
		return MailServiceImpl.mailConnectIMAP("mail.csie.ntnu.edu.tw", "tor@csie.ntnu.edu.tw", "tor@CSIE@6690");
	}

	public IMAPFolder getIMAPFolder(IMAPStore store) throws Exception {
		return MailServiceImpl.getIMAPFolder(store);
	}

	public HashMap<String, String> getSmtp() {
		HashMap<String, String> smtp = new HashMap<String, String>();

		smtp.put("host", "mail.csie.ntnu.edu.tw");
		smtp.put("username", "tor@csie.ntnu.edu.tw");
		smtp.put("password", "tor@CSIE@6690");

		return smtp;
	}

	public void parseMessagePOP3(POP3Store stroe, POP3Folder folder)
			throws MessagingException, IOException, CsvException {
		folder.close(true);
		stroe.close();
	}

	public void parseMessageIMAP(IMAPStore stroe, IMAPFolder folder)
			throws MessagingException, IOException, CsvException {
		Message[] messages = folder.getMessages();

		HashMap<String, String> smtp = getSmtp();
		HashMap<String, String> mail = new HashMap<String, String>();

		mail.put("from", "tor@csie.ntnu.edu.tw");

		if (messages == null || messages.length < 1)
			throw new MessagingException("未找到要解析的信件!");

		// 解析所有邮件
		for (int i = 0, count = messages.length; i < count; i++) {
			MimeMessage msg = (MimeMessage) messages[i];

//			if (Verify.checkSubjectByRegister(MailServiceImpl.getSubject(msg)) && !MailServiceImpl.isSeen(msg)) {
			if (Verify.checkSubjectByRegister(MailServiceImpl.getSubject(msg))) {
				MailServiceImpl.mailMessages(msg);

				int headerCount = 11;

				JSONArray registerColumns = getTestObject();

				boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);

				if (isContainerAttachment) {
					JSONObject fileInfo = getFileType(msg);

					if (fileInfo.getBoolean("status")) {

						String fileType = fileInfo.getString("type");

						String newFile = MailServiceImpl.saveAttachment(msg, mailFilePath, fileType);

						JSONObject content = readAttachment(fileType, newFile, headerCount);

						System.out.println(content.toString());
						if (content.getBoolean("status")) {
							errorMessage = RegisterServiceImpl.save(content.getJSONArray("text"),
									MailServiceImpl.getFrom(msg), registerColumns);
							System.out.println(errorMessage);

//							if (errorMessage == null || "".equals(errorMessage)) {
//								mail.put("receive", MailServiceImpl.getFrom(msg));
//								mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊成功");
//								mail.put("content", "所有資料已報名完成");
//							} else {
//								mail.put("receive", MailServiceImpl.getFrom(msg));
//								mail.put("from", "tor@csie.ntnu.edu.tw");
//								if (errorMessage == "檔案有問題") {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊檔案有問題");
//									mail.put("content", "請確認檔案是否有問題");
//								} else if (errorMessage == "header naming error") {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊欄位有誤");
//									mail.put("content", "請確認註冊欄位名稱是否有誤");
//								} else {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊資料有誤");
//									mail.put("content", errorMessage);
//								}
//							}
						} else {
							if (content.get("msg").equals("header count error")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊欄位有誤");
								mail.put("content", "請確認報名欄位數量是否有誤");
							} else if (content.get("msg").equals("over row data")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-註冊筆數過多");
								mail.put("content", "註冊筆數不可超過十萬筆 (含十萬筆)");
							} else {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-附件檔案異常");
								mail.put("content", "附件檔案異常請重新寄送檔案");
							}
						}
					} else {
						mail.put("subject", MailServiceImpl.getSubject(msg) + "-附件檔案類型錯誤");
						mail.put("content", "請確認附件檔案類型是否符合規定");
					}

				} else {
					mail.put("subject", MailServiceImpl.getSubject(msg) + "-未夾帶附件檔案");
					mail.put("content", "請確認信件是否夾帶附件檔");
				}

//				MailServiceImpl.sendEmail(smtp, mail);
				errorMessage = null;
			}

//			MailServiceImpl.setMailRead(msg, true);
		}
		folder.close(true);
		stroe.close();
	}

	public JSONObject getFileType(MimeMessage msg) throws JSONException, IOException, MessagingException {
		JSONObject result = new JSONObject();

		if (MailServiceImpl.checkFileType(msg, "xlsx")) {
			result.put("status", true);
			result.put("type", "xlsx");
		} else if (MailServiceImpl.checkFileType(msg, "xls")) {
			result.put("status", true);
			result.put("type", "xls");
		} else if (MailServiceImpl.checkFileType(msg, "ods")) {
			result.put("status", true);
			result.put("type", "ods");
		} else {
			result.put("status", false);
			result.put("type", "");
		}

		return result;
	}

	public JSONObject readAttachment(String fileType, String newFile, int headerCount) throws IOException {
		JSONObject result = new JSONObject();

		if (fileType.equals("xlsx") || fileType.equals("xls")) {
			result = MSOfficeServiceImpl.readExcel(newFile, fileType, mailFilePath, headerCount);
		} else if (fileType.equals("ods")) {
			result = OpenOfficeServiceImpl.readODS(newFile, mailFilePath, headerCount);
		}

		return result;
	}

	public JSONArray getTestObject() {
		JSONArray text = new JSONArray();

		JSONObject result = new JSONObject();

		result.put("columnKey", "schoolNumber");
		result.put("columnName", "學校代碼");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "name");
		result.put("columnName", "姓名");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "email");
		result.put("columnName", "信箱");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "tel");
		result.put("columnName", "公務電話");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "TMO");
		result.put("columnName", "數奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "IPHO");
		result.put("columnName", "物奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "TWICHO");
		result.put("columnName", "化奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "CTBO");
		result.put("columnName", "生奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "TOI");
		result.put("columnName", "資奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "IESO");
		result.put("columnName", "地奧");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "TWIJSO");
		result.put("columnName", "國中奧林匹亞");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);

		return text;
	}
}
