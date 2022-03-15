package com.olympic.mailParser.Service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.MailService;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import com.sun.mail.util.MailSSLSocketFactory;

@Service
public class MailServiceImpl implements MailService {
	
	public  String fileName = "";

    public POP3Store mailConnectPOP3(String host, String mail, String pass) throws Exception {

        Properties props = new Properties();
        props.setProperty("mail.popStore.protocol", "pop3");       
        props.setProperty("mail.pop3.port", "995"); 

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.pop3.ssl.enable",true);
        props.put("mail.pop3.ssl.socketFactory",sf);

        props.setProperty("mail.pop3.host", host);

        Session session = Session.getInstance(props);
        POP3Store store = (POP3Store) session.getStore("pop3");

        store.connect(host, mail, pass);
        
        return store;
    
    }
    
    public POP3Folder getPOP3Folder (POP3Store store) throws MessagingException {
    	POP3Folder folder = (POP3Folder) store.getFolder("INBOX");

        folder.open(Folder.READ_WRITE); 
        
        return  folder;
    }
    
    public IMAPStore mailConnectIMAP(String host, String mail, String pass) throws Exception {

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");       
        props.setProperty("mail.imap.host", host);

        Session session = Session.getInstance(props);
        IMAPStore store = (IMAPStore) session.getStore("imap");

        store.connect(host, mail, pass);
        
        return store;
    
    }
    
    public IMAPFolder getIMAPFolder (IMAPStore store) throws MessagingException {
    	IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");

        folder.open(Folder.READ_WRITE); 
        
//      System.out.println("未讀信件: " + folder.getUnreadMessageCount());
//
//      System.out.println("刪除信件數: " + folder.getDeletedMessageCount());
//      System.out.println("新信件: " + folder.getNewMessageCount());
//
//      System.out.println("信件總數: " + folder.getMessageCount());
        
        return  folder;
    }
    

    /**
     * 取得信件主旨
     *
     * @param msg 信件內容
     * @return 解碼後的信件主旨
     */
    public  String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 取得發送人
     *
     * @param msg 信件內容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public  String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        if (froms.length < 1)
            throw new MessagingException("沒有發送人!");

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    /**
     * 根據收件人類型，獲取信件收件人、抄送和密送地址。如果收件人類型為空，則獲得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  副本</p>
     * <p>Message.RecipientType.BCC 密件副本</p>
     *
     * @param msg  信件内容
     * @param type 收件人類型
     * @return 收件人1 <信件地址1>, 收件人2 <信件地址2>, ...
     * @throws MessagingException
     */
    public String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
            throw new MessagingException("没有收件人!");
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress) address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length() - 1); //刪除最後的逗點

        return receiveAddress.toString();
    }

    /**
     * 取得信件發送時間
     *
     * @param msg 信件內容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        if (msg.getSentDate() == null)
            return "";

        if (pattern == null || "".equals(pattern))
            pattern = "yyyy年MM月dd日 E HH:mm ";

        return new SimpleDateFormat(pattern).format(msg.getSentDate());
    }

    /**
     * 判斷是否有附件
     *
     * @return 信件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 判断信件是否已讀
     *
     * @param msg 信件内容
     * @return 如果信件已讀返回true, 否则返回false
     * @throws MessagingException
     */
    public boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 取得信件優先等級
     *
     * @param msg 信件內容
     * @return 1(High):緊急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "緊急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 取得信件文本内容
     *
     * @param part    信件
     * @param content 存储信件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本類型的附件，通過getContent方法可以取到文本內容，但這不是我們需要的結果，所以在這裡要做判斷
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }
    
    /**
     * 設定信件讀取狀態
     */
    public void setMailRead(MimeMessage msg, Boolean flag) throws MessagingException {
    	msg.setFlag(Flags.Flag.SEEN, flag);
    }

    /**
     * 保存附件
     *
     * @param part    信件中多個組合體中的其中一個組合體
     * @param destDir 附近檔案路徑
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CsvValidationException 
     */
    public String saveAttachment(Part part, String destDir) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException, CsvValidationException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //複雜信件
            //含多個信件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //取得其中一個信件
                BodyPart bodyPart = multipart.getBodyPart(i);
                //取得的信件也是複雜信件
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    fileName = saveFile(is, destDir, decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, destDir);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        fileName = saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
            
            return fileName;
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), destDir);
        }
		return destDir;
    }

    /**
     * 儲存附件檔案
     *
     * @param is       輸入
     * @param fileName 檔案名稱
     * @param destDir  檔案路徑
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CsvValidationException 
     */
    public String saveFile(InputStream is, String destDir, String fileName) throws FileNotFoundException, IOException, CsvValidationException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    	
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File(destDir + dtf.format(LocalDateTime.now()).hashCode()+".csv")));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
        
        return dtf.format(LocalDateTime.now()).hashCode()+".csv";
    }

    /**
     * 內容解碼
     *
     * @param encodeText 解碼MimeUtility.encodeText(String text)方法編碼後的內容
     * @return 解碼後的內容
     * @throws UnsupportedEncodingException
     */
    public String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
    
    public void sendEmail (HashMap<String, String> smtp, HashMap<String, String> mail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtp.get("host"));
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props,
           new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(smtp.get("username"), smtp.get("password"));
  	   }
           });

        try {
	  	   Message message = new MimeMessage(session);
	  	
	  	   message.setFrom(new InternetAddress(mail.get("from")));
	  	
	  	   message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.get("receive")));
	  	
	  	   message.setSubject(mail.get("subject"));

	  	   message.setText(mail.get("content"));

	  	   Transport.send(message);

        } catch (MessagingException e) {
           throw new RuntimeException(e);
        }
     }
}
