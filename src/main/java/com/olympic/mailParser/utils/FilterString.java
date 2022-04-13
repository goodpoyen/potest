package com.olympic.mailParser.utils;

import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class FilterString {
	public String cleanXSS(String str) {
		str = str.toLowerCase();

		if (Objects.isNull(str)) {
			return str;
		}

		str = str.replaceAll("<", "").replaceAll(">", "");
		str = str.replaceAll("\\(", "").replaceAll("\\)", "");
		str = str.replaceAll("'", "");
		str = str.replaceAll(";", "");
		str = str.replaceAll("eval((.*))", "");
		str = str.replaceAll("[\"'][s]*javascript:(.*)[\"']", "");
		str = str.replaceAll("<script>", "");
		str = str.replaceAll("iframe", "");
		str = str.replaceAll("onmouseover", "");
		str = str.replaceAll("onmousemove", "");

		return str;
	}

	public String cleanSqlInjection(String str) {
		str = str.toLowerCase();
		
		if (Objects.isNull(str)) {
			return str;
		}

		String badStr = "and|exec|execute|insert|select|delete|update|count|drop|*|%|chr|mid|master|truncate|"
				+ "char|declare|sitename|net user|xp_cmdshell|or|like'|create|"
				+ "table|from|grant|use|group_concat|column_name|"
				+ "information_schema.columns|table_schema|union|where|order";
		
		String[] badStrs = badStr.split("\\|");

		for (int i = 0; i < badStrs.length; i++) {
			if (str.indexOf(badStrs[i]) >= 0) {
				str = str.replaceAll(badStrs[i], "");
			}
		}
		return str;
	}
}
