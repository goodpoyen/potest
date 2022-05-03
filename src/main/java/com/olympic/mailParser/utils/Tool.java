package com.olympic.mailParser.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class Tool {

	public String getMD5(String password) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");

		byte[] messageDigest = md.digest(password.getBytes());

		BigInteger no = new BigInteger(1, messageDigest);

		String hashtext = no.toString(16);
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		return hashtext;
	}
	
	public String getRandomString(int i, String rule) {
		byte[] bytearray = new byte[256];
		String mystring;
		StringBuffer thebuffer;
		String theAlphaNumericS;

		new Random().nextBytes(bytearray);

		mystring = new String(bytearray, Charset.forName("UTF-8"));

		thebuffer = new StringBuffer();

		theAlphaNumericS = mystring.replaceAll(rule, "");

		for (int m = 0; m < theAlphaNumericS.length(); m++) {

			if (Character.isLetter(theAlphaNumericS.charAt(m)) && (i > 0)
					|| Character.isDigit(theAlphaNumericS.charAt(m)) && (i > 0)) {

				thebuffer.append(theAlphaNumericS.charAt(m));
				i--;
			}
		}

		return thebuffer.toString();
	}

	public String shuffle(String word) {
		ArrayList<Character> list = new ArrayList<Character>();
		for (int i = 0; i < word.length(); i++) {
			char currentCharacter = word.charAt(i);
			list.add(currentCharacter);
		}

		Collections.shuffle(list);

		String str = "";
		for (Character word1 : list) {
			str += word1;
		}

		return str;
	}
	
	public String getRandomSymbol() {

        Random random = new Random();

        String setOfCharacters = "@!#=";

        int randomInt = random.nextInt(setOfCharacters.length());
        char randomChar = setOfCharacters.charAt(randomInt);

       return String.valueOf(randomChar);
    }
}
