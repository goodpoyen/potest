package com.olympic.mailParser.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
import com.olympic.mailParser.Service.impl.AES256ServiceImpl;
import com.olympic.mailParser.Service.impl.CSVFileServiceImpl;
import com.olympic.mailParser.Service.impl.MSOfficeServiceImpl;
import com.olympic.mailParser.Service.impl.SignUpMailParserServiceImpl;
import com.olympic.mailParser.Service.impl.TOISignUpServiceImpl;
import com.olympic.mailParser.Service.impl.ZipFileServiceImpl;
import com.olympic.mailParser.utils.Tool;
import com.olympic.mailParser.utils.Verify;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

@RestController
public class TestController {

	@Autowired
	SignUpMailParserServiceImpl SignUpMailParserServiceImpl;

	@Autowired
	ZipFileServiceImpl ZipFileServiceImpl;

	@Autowired
	private OlympicScheduleRepository OlympicScheduleRepository;

	@Autowired
	private Verify Verify;
	
	@Autowired
	private Tool Tool;

	@Autowired
	private CSVFileServiceImpl CSVFileServiceImpl;

	@Autowired
	private MSOfficeServiceImpl MSOfficeServiceImpl;

	@Autowired
	private TOISignUpServiceImpl TOISignUpServiceImpl;
	
	@Autowired
	private AES256ServiceImpl AES256ServiceImpl;

	@Value("${mailFilePath}")
	private String mailFilePath;

	private String olyId = "3";

	@GetMapping("/test")
	public String home() throws Exception {
		IMAPStore store = SignUpMailParserServiceImpl.mailConnectIMAP();
		IMAPFolder folder = SignUpMailParserServiceImpl.getIMAPFolder(store);

		SignUpMailParserServiceImpl.parseMessageIMAP(store, folder);

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

//		String a = "A3234";
//		System.out.println("性別錯誤:" + Verify.checkIdCard(a));
//		
//		a = "A1234";
//		System.out.println("1完全正確:" + Verify.checkIdCard(a));
//		
//		a = "A2234";
//		System.out.println("2完全正確:" + Verify.checkIdCard(a));
		
//		List<String> headerData = new ArrayList();
//		
//		headerData.add("123");
//		headerData.add("456");
//		headerData.add("789");
//		
//		System.out.println(headerData.get(1));
//		System.out.println(headerData.remove(1));
//		System.out.println(headerData.get(1));
//		
//
//		String b = Tool.getRandomString(3, "[^A-Z]");
//		String c = Tool.getRandomString(3, "[^a-z]");
//		String d = Tool.getRandomString(3, "[^0-9]");
////		String e = Tool.getRandomString(1, "[^\\W]");
//		String e = Tool.getRandomSymbol();
//		String pwd = b + c + d + e;
//
//		System.out.println("隨機產生: "+ pwd);
//		pwd = Tool.shuffle(pwd);
//		System.out.println("重新排列: "+ pwd);
//		System.out.println("MD5: "+ Tool.getMD5(pwd));
//		System.out.println("------------------------");
		
		AES256ServiceImpl.setKey("uBdUx82vPHkDKb284d7NkjFoNcKWBuka", "c558Gq0YQK2QUlMc");
		System.out.println(AES256ServiceImpl.encode("A1235"));
		System.out.println(AES256ServiceImpl.decode("2db38a0ce3d74f740d653b67addf9bb1b0f1b62bf7966bff1d249f6bebe19394"));
//		AES256ServiceImpl.decode("78754f08fef7104dcdc4860108c11a6c");
		
		return "db test";
	}
}
