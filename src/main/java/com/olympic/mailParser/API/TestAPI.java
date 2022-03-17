package com.olympic.mailParser.API;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestAPI {

	@PostMapping("/test1")
	JSONObject newEmployee(@RequestBody String data) {
		JSONObject object = new JSONObject(data);
		
		System.out.println(object);
		return object;
	}
}
