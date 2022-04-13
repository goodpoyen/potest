package com.olympic.mailParser.controller;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olympic.mailParser.DAO.Repository.OlympicScheduleRepository;
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

		try {
			JSONObject result = new JSONObject();
			
			
			List<List> myArrayList = new ArrayList(); 
			
			Workbook wb = null;

			SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");  

			String type = "xls";
			if (type.equals("xls")) {
				POIFSFileSystem pfs = new POIFSFileSystem(new FileInputStream(mailFilePath + "test3p.xls"));
				Biff8EncryptionKey.setCurrentUserPassword("123456");
				wb = WorkbookFactory.create(pfs);
			} else if (type.equals("xlsx")) {
				FileInputStream in = new FileInputStream(mailFilePath + "test2p.xlsx");
				POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
				EncryptionInfo encInfo = new EncryptionInfo(poifsFileSystem);
				Decryptor decryptor = Decryptor.getInstance(encInfo);
				decryptor.verifyPassword("123456");
				wb = WorkbookFactory.create(decryptor.getDataStream(poifsFileSystem));
			}

			Sheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getPhysicalNumberOfRows(); // 獲取總行數

			for (int r = 0; r < rowCount; r++) {
				
				Row row = sheet.getRow(r);
				int cellCount = row.getPhysicalNumberOfCells(); // 獲取總列數
				List data = new ArrayList(); 
				// 遍歷每一列
				for (int c = 0; c < cellCount; c++) {
					Cell cell = row.getCell(c);
					String cellType = cell.getCellType().toString();
					String cellValue = null;
					switch (cellType) {
						case "STRING": // 文本
							cellValue = cell.getStringCellValue();
							break;
						case "NUMERIC": // 數字、日期
							if (DateUtil.isCellDateFormatted(cell)) {
								cellValue = fmt.format(cell.getDateCellValue()); // 日期型
							} else {
								cellValue = String.valueOf(cell.getNumericCellValue()); // 數字
							}
							break;
						default:
							cellValue = "";
					}
					data.add(cellValue);
//					myArrayList.add(data);
//					System.out.println(cellValue);
				}
				myArrayList.add(data);
				
			}
			JSONArray jsArray = new JSONArray(myArrayList);
			
			result.put("status", false);
			result.put("resultData", jsArray);
//			System.out.println(result);
			
			JSONArray array = result.getJSONArray("resultData");
			
			for (int i = 0; i < array.length(); i++) {
				JSONArray item2 = array.getJSONArray(i);
				
				for (int j = 0; j < item2.length(); j++) {
					item2.get(j);
					System.out.println(item2.get(j));
				}
					
			}
			
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return "db test";
	}

}
