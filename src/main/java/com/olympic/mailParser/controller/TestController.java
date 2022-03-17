package com.olympic.mailParser.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
import com.olympic.mailParser.Service.impl.MailParserServiceImpl;
import com.olympic.mailParser.until.Verify;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@RestController
public class TestController {
	
	@Autowired 
	MailParserServiceImpl MailParserServiceImpl;
	
	@Autowired
    private  OlympicScheduleRepository OlympicScheduleRepository;
	
	@Autowired
	private Verify Verify;
	
	@GetMapping("/test")
	public String home () throws Exception {
//		IMAPStore store = MailParserServiceImpl.mailConnectIMAP();
//		IMAPFolder folder = MailParserServiceImpl.getIMAPFolder(store);
//		
//		MailParserServiceImpl.parseMessageIMAP(store, folder);

		return "finish";
	}
	
	@GetMapping("/hello")
	public String hello () throws Exception {
		
//		System.out.println("太長: " + Verify.checkPassword("Bigmama@12385486245")); 
//		System.out.println("太短: " + Verify.checkPassword("B@123")); 
//    	System.out.println("沒有特殊符號: " +Verify.checkPassword("Bigmama123")); 
//    	System.out.println("純數字: " +Verify.checkPassword("8888888123"));
//    	System.out.println("純小寫字母: " +Verify.checkPassword("adkdkdfnen"));
//    	System.out.println("純大寫字母: " +Verify.checkPassword("BIDEMAKEJE"));
//    	System.out.println("沒有大寫字母: " +Verify.checkPassword("bigmama@123")); 
//    	System.out.println("沒有小寫字母: " +Verify.checkPassword("BIGMAMA@123")); 
//    	System.out.println("沒有數字: " +Verify.checkPassword("BIGMAMA@asb")); 
//    	System.out.println("只有符號: " +Verify.checkPassword("@@@@@@@@@@@")); 
//    	System.out.println("符號+數字: " +Verify.checkPassword("@@@@@@@@123")); 
//    	System.out.println("符號+小寫字母: " +Verify.checkPassword("@@@@@@@@abc"));
//    	System.out.println("符號+大寫字母: " +Verify.checkPassword("@@@@@@@@BMD"));
//    	System.out.println("符號+大小寫字母: " +Verify.checkPassword("@@a@@@@@BMD"));
//    	System.out.println("符號+大寫字母+數字: " +Verify.checkPassword("@@1@@@@@BMD"));
//    	System.out.println("符號+小寫字母+數字: " +Verify.checkPassword("@@@5@@@@abc"));
//    	System.out.println("正常: " +Verify.checkPassword("Bigmama@123"));

		return "hello word3";
	}
	
	@GetMapping("/db")
	public String db () throws Exception {
		
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//		
//		List<Map<String,Object>> schedule = OlympicScheduleRepository.getOlympicSchedule("[TOI]奧林匹亞-022初選", dtf.format(LocalDateTime.now()).toString());
//		
//		System.out.println(schedule.size());
//		
//		for(Map<String,Object> m : schedule){    
//            for(String s : m.keySet()){    
//            	System.out.println(s);
//                System.out.println(m.get(s));
//            }
//        }
		
//		System.out.println(schedule.getSignupName());

		return "db test";
	}

}
