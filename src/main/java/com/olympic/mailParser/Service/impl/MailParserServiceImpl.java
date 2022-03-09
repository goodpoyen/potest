package com.olympic.mailParser.Service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.DAO.Entity.SignUpStudents;
import com.olympic.mailParser.DAO.Repository.SignUpStudentsRepository;
import com.olympic.mailParser.Service.MailParserService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

@Service
public class MailParserServiceImpl implements MailParserService {
	
	@Autowired
    private  SignUpStudentsRepository signUpStudentsRepository;
	
	@Autowired
	private MailServiceImpl MailServiceImpl;
	
	
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
            
            if (MailServiceImpl.getSubject(msg).contains("奧林匹亞") && !MailServiceImpl.isSeen(msg)) {
//            if (MailServiceImpl.getSubject(msg).contains("奧林匹克") ) {
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
                if (isContainerAttachment) {
                	fileName = MailServiceImpl.saveAttachment(msg, "C:\\testData\\");   
                }
                StringBuffer content = new StringBuffer(30);
                MailServiceImpl.getMailTextContent(msg, content);
                System.out.println("信件正文：" + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
                System.out.println("------------------第" + msg.getMessageNumber() + "封信件解析結束-------------------- ");
                System.out.println();
                
                fileReader(fileName);
                deleteFile(new File("C:\\testData\\" + fileName));
            }
            
            MailServiceImpl.setMailRead(msg, true);
        }
        folder.close(true);
        stroe.close();
    }

	public void fileReader(String fileName) {
        String filePath = "C:\\testData\\" + fileName;
        
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
//	            		for(String s : nextRecord)
//	        				if(null != s && !s.equals(""))
//	        					
//	        					System.out.print(s);
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
    	SignUpStudents student = signUpStudentsRepository.findByNameAndIdCard(SingUpdata[0], SingUpdata[1]);
    	
    	if (student == null) {
    		SignUpStudents SignUpStudents = new SignUpStudents();
    		
        	SignUpStudents.setName(SingUpdata[0]);
        	SignUpStudents.setIdCard(SingUpdata[1]);
        	
        	signUpStudentsRepository.save(SignUpStudents);
    	}else {
    		signUpStudentsRepository.save(student);
    	}
        
    }
}
