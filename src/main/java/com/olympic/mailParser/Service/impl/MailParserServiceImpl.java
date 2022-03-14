package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;
import com.olympic.mailParser.DAO.Repository.SignUpStudentsRepository;
import com.olympic.mailParser.Service.MailParserService;
import com.olympic.mailParser.until.Verify;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

@Service
public class MailParserServiceImpl implements MailParserService {
	
	@Value("${mailFilePath}")
	private String mailFilePath;
	
	@Autowired
    private  SignUpStudentsRepository signUpStudentsRepository;
	
	@Autowired
	private MailServiceImpl MailServiceImpl;
	
	@Autowired
	private Verify Verify;
	
	@Autowired
	private AES256ServiceImpl AES256ServiceImpl;
	
	private String errorMessage;
	
	
	public POP3Store mailConnectPOP3 () throws Exception {
		return MailServiceImpl.mailConnectPOP3("mail.csie.ntnu.edu.tw", "tor@csie.ntnu.edu.tw", "tor@CSIE@6690");
	}
	
	public POP3Folder getPOP3Folder (POP3Store store) throws Exception {
		return MailServiceImpl.getPOP3Folder(store);
	}
	
	public IMAPStore mailConnectIMAP () throws Exception {
		return MailServiceImpl.mailConnectIMAP("mail.csie.ntnu.edu.tw", "tor@csie.ntnu.edu.tw", "tor@CSIE@6690");
	}
	
	public IMAPFolder getIMAPFolder (IMAPStore store) throws Exception {
		return MailServiceImpl.getIMAPFolder(store);
	}
	
	public HashMap<String, String> getSmtp () {
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
    public void parseMessagePOP3(POP3Store stroe, POP3Folder folder) throws MessagingException, IOException, CsvException {
        folder.close(true);
        stroe.close();
    }
    
    public void parseMessageIMAP(IMAPStore stroe, IMAPFolder folder) throws MessagingException, IOException, CsvException {
    	Message[] messages = folder.getMessages();
    	
    	String fileName = "";
        if (messages == null || messages.length < 1)
            throw new MessagingException("未找到要解析的!");

        // 解析所有邮件
        for (int i = 0, count = messages.length; i < count; i++) {
            MimeMessage msg = (MimeMessage) messages[i];
            
//            if (MailServiceImpl.getSubject(msg).contains("[TOI]奧林匹亞") && !MailServiceImpl.isSeen(msg)) {
            if (MailServiceImpl.getSubject(msg).contains("[TOI]奧林匹亞") ) {
   
            	mailMessages(msg);
            	
                boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);

                if (isContainerAttachment) {
                	fileName = MailServiceImpl.saveAttachment(msg, mailFilePath);   
                }
                
                fileReader(fileName);
                deleteFile(new File(mailFilePath + fileName));
                
                HashMap<String, String> smtp = getSmtp();
                HashMap<String, String> mail = new HashMap<String, String>();
                
                if (errorMessage == null || "".equals(errorMessage)) {
                	mail.put("receive", MailServiceImpl.getFrom(msg));
        			mail.put("from", "tor@csie.ntnu.edu.tw");
        			mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名成功");
        			mail.put("content", "所有資料已報名完成");			
                }else {
                	mail.put("receive", MailServiceImpl.getFrom(msg));
        			mail.put("from", "tor@csie.ntnu.edu.tw");
        			if (errorMessage == "檔案有問題") {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名檔案有問題");
            			mail.put("content", "請確認CSV檔案是否有問題");
        			}else {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名資料有誤");
            			mail.put("content", errorMessage);
        			}
                }
                
                MailServiceImpl.sendEmail(smtp,mail);  
                errorMessage = "";
            }
            
            MailServiceImpl.setMailRead(msg, true);
        }
        folder.close(true);
        stroe.close();
    }
    
    public void mailMessages (MimeMessage msg) throws MessagingException, IOException {
    	System.out.println("------------------解析第" + msg.getMessageNumber() + "封信件-------------------- ");
        System.out.println("主旨: " + MailServiceImpl.getSubject(msg));
        System.out.println("發件人: " + MailServiceImpl.getFrom(msg));
        System.out.println("收件人：" + MailServiceImpl.getReceiveAddress(msg, null));
        System.out.println("發送時間：" + MailServiceImpl.getSentDate(msg, null));
        System.out.println("是否已讀：" + MailServiceImpl.isSeen(msg));
        System.out.println("信件優先等級."+ "：" + MailServiceImpl.getPriority(msg));
        System.out.println("信件大小：" + msg.getSize() * 1024 + "kb");
        
        boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);
        System.out.println("是否包含附件：" + isContainerAttachment);
        
//        StringBuffer content = new StringBuffer(30);
//        MailServiceImpl.getMailTextContent(msg, content);
//        System.out.println("信件正文：" + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
        System.out.println("------------------第" + msg.getMessageNumber() + "封信件解析結束-------------------- ");
        System.out.println();
    }

	public void fileReader(String fileName) {
        String filePath = mailFilePath + fileName;
        
        FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(fileInputStream, "utf-8");

            CSVReader csvReader = new CSVReader(reader);

    		String[] nextRecord;
    		int line = 0;
            while ((nextRecord = csvReader.readNext()) != null) {

            	if (line != 0) {
            		saveSingUpData(nextRecord);
            	}
                
                line ++;
            }
		} catch (CsvValidationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void deleteFile(File file) {
		if(file.exists()) {
			System.gc();
			if(file.isFile()){
				file.delete();
			}else{
				File[] listFiles = file.listFiles();
				for (File file2 : listFiles) {
					deleteFile(file2);
				}
			}
			file.delete();
		}else {
			System.out.println("該file路徑不存在！！");
		}
	}
    
    public void saveSingUpData(String[] SingUpdata) {  
    	try {
	    	for (String signUpValue : SingUpdata) {
				if (signUpValue.isEmpty()) {
					errorMessage += SingUpdata[1] + "-資料遺漏" + "\r\n";
					return;
				}
			}
	    	
	    	SignUpStudents student = signUpStudentsRepository.findByNameAndIdCard(SingUpdata[1], AES256ServiceImpl.encode(SingUpdata[2]));
	
	    	if (student == null) {
	    		student = new SignUpStudents();
	    	}
	    	
	    	student.setOlympic(SingUpdata[0]);	
	    	student.setName(SingUpdata[1]);
	    	student.setIdCard(SingUpdata[2]);
	    	student.setSchoolName(SingUpdata[3]);
	    	student.setGrade(SingUpdata[4]);
	    	student.setBirthday(SingUpdata[5]);
	    	student.setEmail(SingUpdata[6]);
	    	student.setGender(SingUpdata[7]);
	    	
	    	if (checkSignUpData(student)) {
	    		student.setIdCard(AES256ServiceImpl.encode(SingUpdata[2]));
	    		student.setBirthday(AES256ServiceImpl.encode(SingUpdata[5]));
	    		student.setEmail(AES256ServiceImpl.encode(SingUpdata[6]));
	    		signUpStudentsRepository.save(student);	
	    	}else {
	    		errorMessage += "\r\n";
	    	}
    	}catch (Exception e) {
    		errorMessage = "檔案有問題";
		}

    }
    
    public Boolean checkSignUpData(SignUpStudents student) {
    	Boolean status = true;
    	String error = "";

    	if (!Verify.checkIdCard(student.getIdCard())) {
    		status = false;
    		error += "身分證有誤;";
    	}
    	
    	if (!Verify.checkValue(Integer.parseInt(student.getGrade()), 7, 12)) {
    		status = false;
    		error += "年級有誤;";
    	}
    	
    	if (!Verify.checkDate(student.getBirthday())) {
    		status = false;
    		error += "出生日期有誤;";
    	}

    	if (!Verify.checkEmail(student.getEmail())) {
    		status = false;
    		error += "信箱有誤;";
    	}
    	

//    	if (!Verify.checkValue(Integer.parseInt(student.getGender()), 1, 2)) {
//    		status = false;
//    		errorMessage += "性別有誤;";
//    	}
    	
    	if (!status) {
    		if (errorMessage == null) {
    			errorMessage = student.getName() + "-" + error;
    		}else {
    			errorMessage += student.getName() + "-" + error;
    		}
    	}
    	
    	return status;
    }
}
