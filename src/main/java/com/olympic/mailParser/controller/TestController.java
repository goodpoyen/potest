package com.olympic.mailParser.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
import com.olympic.mailParser.Service.impl.CSVFileServiceImpl;
import com.olympic.mailParser.Service.impl.MSOfficeServiceImpl;
import com.olympic.mailParser.Service.impl.MailParserServiceImpl;
import com.olympic.mailParser.Service.impl.TOISignUpServiceImpl;
import com.olympic.mailParser.Service.impl.ZipFileServiceImpl;
import com.olympic.mailParser.utils.Verify;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@RestController
public class TestController {

	@Autowired
	MailParserServiceImpl MailParserServiceImpl;

	@Autowired
	ZipFileServiceImpl ZipFileServiceImpl;

	@Autowired
	private OlympicScheduleRepository OlympicScheduleRepository;

	@Autowired
	private Verify Verify;

	@Autowired
	private CSVFileServiceImpl CSVFileServiceImpl;

	@Autowired
	private MSOfficeServiceImpl MSOfficeServiceImpl;

	@Autowired
	private TOISignUpServiceImpl TOISignUpServiceImpl;

	@Value("${mailFilePath}")
	private String mailFilePath;

	private String olyId = "3";

	@GetMapping("/test")
	public String home() throws Exception {
		IMAPStore store = MailParserServiceImpl.mailConnectIMAP();
		IMAPFolder folder = MailParserServiceImpl.getIMAPFolder(store);

		MailParserServiceImpl.parseMessageIMAP(store, folder);

		return "finish";
	}

	@GetMapping("/hello")
	public String hello() throws Exception {

		return "hello word3";
	}

	@GetMapping("/db")
	public String db() throws Exception {
		System.out.println(Verify.getColumnSet());

//		IWorkPackageParser iWorkParser = new IWorkPackageParser();
//		InputStream  inputstream = new FileInputStream(new File(mailFilePath + "test13.numbers"));
//        Metadata metadata = new Metadata(); 
//        ContentHandler handler = new BodyContentHandler(); 
//        ParseContext pcontext = new ParseContext();
//        pcontext.set(Parser.class, new AutoDetectParser()); 
//        iWorkParser.parse(inputstream, handler, metadata, pcontext); 
// 
//        String content = handler.toString(); 
//        System.out.println("Contents of the document:" + content);

//		String a = "A3234";
//		System.out.println("性別錯誤:" + Verify.checkIdCard(a));
//		
//		a = "A1234";
//		System.out.println("1完全正確:" + Verify.checkIdCard(a));
//		
//		a = "A2234";
//		System.out.println("2完全正確:" + Verify.checkIdCard(a));

		return "db test";
	}

}
