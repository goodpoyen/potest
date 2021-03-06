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
     * @param messages parser??????
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
            throw new MessagingException("?????????????????????!");

        // ??????????????????
        for (int i = 0, count = messages.length; i < count; i++) {
            MimeMessage msg = (MimeMessage) messages[i];
            
//            if (MailServiceImpl.getSubject(msg).contains("[TOI]????????????") && !MailServiceImpl.isSeen(msg)) {
            if (Verify.checkSubject(MailServiceImpl.getSubject(msg))) {
//            if (MailServiceImpl.getSubject(msg).contains("[TOI]??????") ) {
   
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
        			mail.put("subject", MailServiceImpl.getSubject(msg) + "-????????????");
        			mail.put("content", "???????????????????????????");			
                }else {
                	mail.put("receive", MailServiceImpl.getFrom(msg));
        			mail.put("from", "tor@csie.ntnu.edu.tw");
        			if (errorMessage == "???????????????") {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-?????????????????????");
            			mail.put("content", "?????????CSV?????????????????????");
        			}
        			else if (errorMessage == "?????????????????????") {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-???????????????????????????");
            			mail.put("content", "?????????????????????????????????");
        			}
        			else {
        				mail.put("subject", MailServiceImpl.getSubject(msg) + "-??????????????????");
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
    	System.out.println("------------------?????????" + msg.getMessageNumber() + "?????????-------------------- ");
        System.out.println("??????: " + MailServiceImpl.getSubject(msg));
        System.out.println("?????????: " + MailServiceImpl.getFrom(msg));
        System.out.println("????????????" + MailServiceImpl.getReceiveAddress(msg, null));
        System.out.println("???????????????" + MailServiceImpl.getSentDate(msg, null));
        System.out.println("???????????????" + MailServiceImpl.isSeen(msg));
        System.out.println("??????????????????."+ "???" + MailServiceImpl.getPriority(msg));
        System.out.println("???????????????" + msg.getSize() * 1024 + "kb");
        
        boolean isContainerAttachment = MailServiceImpl.isContainAttachment(msg);
        System.out.println("?????????????????????" + isContainerAttachment);
        
//        StringBuffer content = new StringBuffer(30);
//        MailServiceImpl.getMailTextContent(msg, content);
//        System.out.println("???????????????" + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
        System.out.println("------------------???" + msg.getMessageNumber() + "?????????????????????-------------------- ");
        System.out.println();
    }

    public void fileReader1(String fileName, MimeMessage msg) throws IOException {
        String filePath = mailFilePath + fileName;
        
        Path path = Paths.get(filePath);
        String content = Files.readString(path);
        
        rowData.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");
        
        content = rowData.decode(content);
        
        if (content == null) {
        	errorMessage = "?????????????????????";
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
            		if (errorMessage == "???????????????") {
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
            		if (errorMessage == "???????????????") {
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
			System.out.println("???file?????????????????????");
		}
	}
    
    public void saveSingUpData(String[] SingUpdata, MimeMessage msg) {  
    	for (String signUpValue : SingUpdata) {
			if (signUpValue.isEmpty()) {
				errorMessage += SingUpdata[1] + "-????????????" + "\r\n";
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
