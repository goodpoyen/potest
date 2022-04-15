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
import com.olympic.mailParser.Service.MailParserService;
import com.olympic.mailParser.utils.Verify;
import com.opencsv.exceptions.CsvException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

@Service
public class MailParserServiceImpl implements MailParserService {

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

	private String olyId;

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

	/**
	 * parser
	 *
	 * @param messages parser列表
	 * @throws CsvException
	 */
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

			olyId = getOlympicSchedule(MailServiceImpl.getSubject(msg));
//            if (MailServiceImpl.getSubject(msg).contains("[TOI]奧林匹亞初選") ) {
//            if (!"".equals(olyId) && olyId != null && !MailServiceImpl.isSeen(msg)) {
			if (!"".equals(olyId) && olyId != null) {
				MailServiceImpl.mailMessages(msg);

				boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);

				if (isContainerAttachment) {

					JSONObject fileInfo = getFileType(msg);

					if (fileInfo.getBoolean("status")) {

						String fileType = fileInfo.getString("type");

						String newFile = MailServiceImpl.saveAttachment(msg, mailFilePath, fileType);

						JSONObject content = new JSONObject();
						if (fileType.equals("xlsx") || fileType.equals("xls")) {
							content = MSOfficeServiceImpl.readExcel(newFile, fileType, mailFilePath, "123456", 8);
						} else if (fileType.equals("zip")) {
							content = OpenOfficeServiceImpl.readODS(newFile, mailFilePath, "123456", 8);
						}
//						System.out.println(content.toString());
						if (content.getBoolean("status")) {
							JSONArray array = content.getJSONArray("text");

							for (int index = 0; index < array.length(); index++) {
								if (index == 0) {
									continue;
								}

								JSONArray item = array.getJSONArray(index);

								String[] rowData = new String[item.length()];

								for (int subIndex = 0; subIndex < item.length(); subIndex++) {
									rowData[subIndex] = item.get(subIndex).toString();
								}

								errorMessage = switchOlympic(rowData, msg, index);
							}

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

	public String getOlympicSchedule(String subject) {
		if (Verify.checkSubject(subject)) {
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
				}
			}

			return olyId;
		} else {
			return "";
		}
	}

	public String switchOlympic(String[] SingUpdata, MimeMessage msg, int index)
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
			return TOISignUpServiceImpl.save(SingUpdata, olyId, MailServiceImpl.getFrom(msg), index);
		default:
			return "55";
		}
	}
}
