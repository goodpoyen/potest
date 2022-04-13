package com.olympic.mailParser.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
import com.olympic.mailParser.Service.impl.CSVFileServiceImpl;
import com.olympic.mailParser.Service.impl.MailParserServiceImpl;
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

	@Value("${mailFilePath}")
	private String mailFilePath;

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

		JSONObject result = CSVFileServiceImpl.readerCSV("資訊奧林匹亞4.csv", mailFilePath);
		
		System.out.println(result);

		return "db test";
	}

}
