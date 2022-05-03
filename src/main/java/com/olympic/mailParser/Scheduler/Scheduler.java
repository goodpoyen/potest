package com.olympic.mailParser.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.olympic.mailParser.Service.impl.SignUpMailParserServiceImpl;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@Component
public class Scheduler {
	@Autowired
	private SignUpMailParserServiceImpl SignUpMailParserServiceImpl;

//	@Scheduled(cron = "0 * * * * ?")
//	@Scheduled(initialDelay = 3 * 1000, fixedDelay = 3 * 60 * 1000)
	public void singUpMail() throws Exception {
		IMAPStore store = SignUpMailParserServiceImpl.mailConnectIMAP();
		IMAPFolder folder = SignUpMailParserServiceImpl.getIMAPFolder(store);

		SignUpMailParserServiceImpl.parseMessageIMAP(store, folder);

	}
}
