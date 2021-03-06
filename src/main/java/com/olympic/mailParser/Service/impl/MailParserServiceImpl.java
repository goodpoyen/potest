package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	private MailServiceImpl MailServiceImpl;
	
	@Autowired
	private Verify Verify;
	
	@Autowired
	private AES256ServiceImpl rowData;
	
	@Autowired
	private TOISignUpServiceImpl TOISignUpServiceImpl;
	
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
            if (Verify.checkSubject(MailServiceImpl.getSubject(msg))) {
//            if (MailServiceImpl.getSubject(msg).contains("[TOI]加密") ) {
   
            	mailMessages(msg);
            	
                boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);

                if (isContainerAttachment) {
                	fileName = MailServiceImpl.saveAttachment(msg, mailFilePath);   
                }
                
                fileReader(fileName, msg);
//                fileReader1(fileName, msg);
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
        			}
        			else if (errorMessage == "檔案加密有問題") {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-報名檔案加密有問題");
            			mail.put("content", "請確認加密鑰匙是否有誤");
        			}
        			else {
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

    public void fileReader1(String fileName, MimeMessage msg) throws IOException {
        String filePath = mailFilePath + fileName;
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        rowData.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");
        
        content = rowData.decode(content);
        
        if (content == null) {
        	errorMessage = "檔案加密有問題";
        	return;
        }
        
        FileInputStream fileInputStream;
		try {
			CSVReader csvReader = new CSVReader(new StringReader(content));

    		String[] nextRecord;
    		int line = 0;
            while ((nextRecord = csvReader.readNext()) != null) {

            	if (line != 0) {
            		saveSingUpData(nextRecord, msg);
            		if (errorMessage == "檔案有問題") {
            			break;
            		}
            	}
                
                line ++;
            }
		} catch (CsvValidationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void fileReader(String fileName, MimeMessage msg) {
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
            		saveSingUpData(nextRecord, msg);
            		if (errorMessage == "檔案有問題") {
            			break;
            		}
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
    
    public void saveSingUpData(String[] SingUpdata, MimeMessage msg) {  
    	for (String signUpValue : SingUpdata) {
			if (signUpValue.isEmpty()) {
				errorMessage += SingUpdata[1] + "-資料遺漏" + "\r\n";
				return;
			}
		}
    	
    	if (errorMessage == null || "".equals(errorMessage)) {
			errorMessage = switchOlympic(SingUpdata, msg);
		}else {
			errorMessage += switchOlympic(SingUpdata, msg);
		}
    }
    
    public String switchOlympic (String[] SingUpdata, MimeMessage msg) {
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
    	
    	switch(type) { 
	        case "TOI":
	        	return TOISignUpServiceImpl.save(SingUpdata);
	        default: 
	            return "55"; 
	    }
    }
}
