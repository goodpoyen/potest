package com.olympic.mailParser.Service.impl;

import org.springframework.stereotype.Service;

import com.olympic.mailParser.Service.AES256Service;

@Service
public class AES256ServiceImpl implements AES256Service{
	/**
	 * 金鑰, 256位32個位元組
	**/
    public static final String DEFAULT_SECRET_KEY = "uBdUx82vPHkDKb284d7NkjFoNcKWBuka";
 
    private static final String AES = "AES";
 
	 /**
	  * 初始向量IV, 初始向量IV的長度規定為128位16個位元組, 初始向量的來源為隨機生成.
	 **/
    private static final byte[] KEY_VI = "c558Gq0YQK2QUlMc".getBytes();

    /**
     * 加密解密演算法/加密模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
 
    private static java.util.Base64.Encoder base64Encoder = java.util.Base64.getEncoder();
    private static java.util.Base64.Decoder base64Decoder = java.util.Base64.getDecoder();
 
    static {
    	java.security.Security.setProperty("crypto.policy", "unlimited");
    }
 
    /**
      * AES加密
     */
    public String encode(String content) {
    	try {
    		javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(DEFAULT_SECRET_KEY.getBytes(), AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_VI));
 
            // 獲取加密內容的位元組陣列(這裡要設定為utf-8)不然內容中如果有中文和英文混合中文就會解密為亂碼
            byte[] byteEncode = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
 
            // 根據密碼器的初始化方式加密
            byte[] byteAES = cipher.doFinal(byteEncode);

            // 將加密後的資料轉換為字串
            return base64Encoder.encodeToString(byteAES);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
 
     /**
      * AES解密
     */
     public String decode(String content) {
         try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(DEFAULT_SECRET_KEY.getBytes(), AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
             cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(KEY_VI));

            // 將加密並編碼後的內容解碼成位元組陣列
	        byte[] byteContent = base64Decoder.decode(content);
	        // 解密
	        byte[] byteDecode = cipher.doFinal(byteContent);
	        return new String(byteDecode, java.nio.charset.StandardCharsets.UTF_8);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
         return null;
     }
	 
}
