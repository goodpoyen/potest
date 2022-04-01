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
import com.olympic.mailParser.utils.Verify;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@RestController
public class TestController {

	@Autowired
	MailParserServiceImpl MailParserServiceImpl;

	@Autowired
	private OlympicScheduleRepository OlympicScheduleRepository;

	@Autowired
	private Verify Verify;

	@GetMapping("/test")
	public String home() throws Exception {
//		IMAPStore store = MailParserServiceImpl.mailConnectIMAP();
//		IMAPFolder folder = MailParserServiceImpl.getIMAPFolder(store);
//		
//		MailParserServiceImpl.parseMessageIMAP(store, folder);

		return "finish-marks";
	}

	@GetMapping("/hello")
	public String hello() throws Exception {

		return "hello word3";
	}

	@GetMapping("/db")
	public String db() throws Exception {

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
