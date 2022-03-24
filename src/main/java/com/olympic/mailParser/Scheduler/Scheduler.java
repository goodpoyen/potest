package com.olympic.mailParser.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.olympic.mailParser.Service.impl.MailParserServiceImpl;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@Component
public class Scheduler {
	@Autowired
	private MailParserServiceImpl MailParserServiceImpl;

//	@Scheduled(cron = "0 * * * * ?")
	public void singUpMail() throws Exception {
		IMAPStore store = MailParserServiceImpl.mailConnectIMAP();
		IMAPFolder folder = MailParserServiceImpl.getIMAPFolder(store);

		MailParserServiceImpl.parseMessageIMAP(store, folder);

	}
}
