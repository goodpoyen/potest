package com.olympic.mailParser.Service.impl;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.AES256Service;

@Service
public class AES256ServiceImpl implements AES256Service{
	public static String DEFAULT_SECRET_KEY = "";
 
    private static final String AES = "AES";

    private static byte[] KEY_VI = "".getBytes();

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
 
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public void setKey (String key, String IV) {
    	DEFAULT_SECRET_KEY = key;
    	KEY_VI = IV.getBytes();
    }
 
    public String encode(String content) {
    	try {
    		javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(DEFAULT_SECRET_KEY.getBytes(), AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_VI));
 
            byte[] byteEncode = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            byte[] byteAES = cipher.doFinal(byteEncode);

            return Hex.toHexString(byteAES);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

     public String decode(String content) {
         try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(DEFAULT_SECRET_KEY.getBytes(), AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_VI));

            byte[] byteContent = Hex.decodeStrict(content);

	        byte[] byteDecode = cipher.doFinal(byteContent);
	        return new String(byteDecode, java.nio.charset.StandardCharsets.UTF_8);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
         return null;
     }
	 
}
