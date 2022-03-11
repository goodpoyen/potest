package com.olympic.mailParser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.Service.impl.MailParserServiceImpl;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@RestController
public class TestController {
	
	@Autowired 
	MailParserServiceImpl MailParserServiceImpl;
	
	@GetMapping("/test")
	public String home () throws Exception {
		IMAPStore store = MailParserServiceImpl.mailConnectIMAP();
		IMAPFolder folder = MailParserServiceImpl.getIMAPFolder(store);
		
		MailParserServiceImpl.parseMessageIMAP(store, folder);

		return "finish";
	}
	
//	@GetMapping("/hello")
//	public String hello () throws Exception {
//
//		return "hello word3";
//	}

}
