package com.olympic.mailParser.Service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
import com.olympic.mailParser.Service.SignUpMailParserService;
import com.olympic.mailParser.utils.Verify;
import com.opencsv.exceptions.CsvException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

@Service
public class SignUpMailParserServiceImpl implements SignUpMailParserService {

	@Value("${mailFilePath}")
	private String mailFilePath;

	@Autowired
	private MailServiceImpl MailServiceImpl;

	@Autowired
	private Verify Verify;

	@Autowired
	private TOISignUpServiceImpl TOISignUpServiceImpl;

	@Autowired
	private OlympicScheduleRepository OlympicScheduleRepository;

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

			JSONObject scheduleDataResult = getOlympicScheduleData(MailServiceImpl.getSubject(msg));

			if (scheduleDataResult.getBoolean("status")) {
				MailServiceImpl.mailMessages(msg);

				int headerCount = scheduleDataResult.getInt("headerCount");

				String olyId = scheduleDataResult.getString("olyId");

				JSONArray signupColumns = scheduleDataResult.getJSONArray("signupColumns");

				boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);

				if (isContainerAttachment) {

					JSONObject fileInfo = getFileType(msg);

					if (fileInfo.getBoolean("status")) {

						String fileType = fileInfo.getString("type");

						String newFile = MailServiceImpl.saveAttachment(msg, mailFilePath, fileType);

						JSONObject content = readAttachment(fileType, newFile, headerCount, "123456");

						System.out.println(content.toString());
						if (content.getBoolean("status")) {
							errorMessage = switchOlympic(content.getJSONArray("text"), msg, signupColumns, olyId);
							System.out.println(errorMessage);

//							if (errorMessage == null || "".equals(errorMessage)) {
//								mail.put("receive", MailServiceImpl.getFrom(msg));
//								mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名成功");
//								mail.put("content", "所有資料已報名完成");
//							} else {
//								mail.put("receive", MailServiceImpl.getFrom(msg));
//								mail.put("from", "tor@csie.ntnu.edu.tw");
//								if (errorMessage == "檔案有問題") {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名檔案有問題");
//									mail.put("content", "請確認CSV檔案是否有問題");
//								} else if (errorMessage == "header naming error") {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名欄位有誤");
//									mail.put("content", "請確認報名欄位名稱是否有誤");
//								} else {
//									mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名資料有誤");
//									mail.put("content", errorMessage);
//								}
//							}
						} else {
							if (content.get("msg").equals("Wrong Password")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-附件檔案密碼錯誤");
								mail.put("content", "請確認附件檔密碼是否正確");
							} else if (content.get("msg").equals("not ods file")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-壓縮檔內檔案格式不對");
								mail.put("content", "請確認附件壓縮檔內檔案格式");
							} else if (content.get("msg").equals("header count error")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名欄位有誤");
								mail.put("content", "請確認報名欄位數量是否有誤");
							} else if (content.get("msg").equals("over row data")) {
								mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名筆數過多");
								mail.put("content", "報名筆數不可超過十萬筆 (含十萬筆)");
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
				errorMessage = "";
			}

			MailServiceImpl.setMailRead(msg, true);
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
		} else if (MailServiceImpl.checkFileType(msg, "zip")) {
			result.put("status", true);
			result.put("type", "zip");
		} else {
			result.put("status", false);
			result.put("type", "");
		}

		return result;
	}

	public JSONObject getOlympicScheduleData(String subject) {
		JSONObject result = new JSONObject();

		if (Verify.checkSubject(subject)) {
			JSONArray signupColumns = new JSONArray();

			String olyId = "";

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

			String type = Verify.checkOlympic(subject);

			subject = subject.replace("[" + type + "]" + "奧林匹亞", "");

			List<Map<String, Object>> schedule = OlympicScheduleRepository.getOlympicSchedule(subject,
					dtf.format(LocalDateTime.now()).toString());

			for (Map<String, Object> data : schedule) {
				for (String key : data.keySet()) {
					if (key.equals("oly_id")) {
						olyId = data.get(key).toString();
					}

					if (key.equals("rules")) {
						signupColumns = new JSONArray(data.get(key).toString());
					}
				}
			}
			int headerCount = 0;

			for (int data = 0; data < signupColumns.length(); data++) {
				if (signupColumns.getJSONObject(data).getBoolean("required")
						|| signupColumns.getJSONObject(data).getBoolean("sysRequired")) {
					headerCount++;
				}
			}

			if (!"".equals(olyId) && olyId != null) {
				result.put("status", true);
			} else {
				result.put("status", false);
			}

			result.put("olyId", olyId);
			result.put("signupColumns", signupColumns);
			result.put("headerCount", headerCount);

			return result;
		} else {
			result.put("status", false);
			result.put("olyId", "");
			result.put("signupColumns", new JSONArray());
			result.put("headerCount", 0);

			return result;
		}
	}

	public String switchOlympic(JSONArray SingUpdata, MimeMessage msg, JSONArray signupColumns, String olyId)
			throws UnsupportedEncodingException, MessagingException {
		String type = "";

		try {
			type = Verify.checkOlympic(MailServiceImpl.getSubject(msg));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (type) {
		case "TOI":
			return TOISignUpServiceImpl.save(SingUpdata, olyId, MailServiceImpl.getFrom(msg), signupColumns);
		case "TMO":
			return "";
		case "IPHO":
			return "";
		case "TWICHO":
			return "";
		case "CTBO":
			return "";
		case "IESO":
			return "";
		case "TWIJSO":
			return "";
		default:
			return "55";
		}
	}

	public JSONObject readAttachment(String fileType, String newFile, int headerCount, String password)
			throws IOException {
		JSONObject result = new JSONObject();

		if (fileType.equals("xlsx") || fileType.equals("xls")) {
			result = MSOfficeServiceImpl.readExcel(newFile, fileType, mailFilePath, password, headerCount);
		} else if (fileType.equals("zip")) {
			result = OpenOfficeServiceImpl.readODS(newFile, mailFilePath, password, headerCount);
		}

		return result;
	}

	public JSONArray getTestObject() {
		JSONArray text = new JSONArray();

		JSONObject result = new JSONObject();

		result.put("columnKey", "olympic");
		result.put("columnName", "類別");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "chineseName");
		result.put("columnName", "中文姓名");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "idCard");
		result.put("columnName", "身分證");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "schoolName");
		result.put("columnName", "校名");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "grade");
		result.put("columnName", "年級");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "birthday");
		result.put("columnName", "生日");
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

		result.put("columnKey", "area");
		result.put("columnName", "初選考區");
		result.put("sysRequired", true);
		result.put("required", true);
		result.put("isNull", false);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "englishName");
		result.put("columnName", "英文姓名");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "teacher");
		result.put("columnName", "初選指導老師");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		result.put("columnKey", "remark");
		result.put("columnName", "重要備註");
		result.put("sysRequired", false);
		result.put("required", false);
		result.put("isNull", true);

		text.put(result);
		result = new JSONObject();

		return text;
	}
}