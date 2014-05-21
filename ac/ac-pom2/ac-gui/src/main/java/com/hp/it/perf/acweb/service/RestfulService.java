package com.hp.it.perf.acweb.service;

import org.json.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestfulService {

	private RestTemplate template = new RestTemplate();
	 
	public JSONObject query(String url) throws JSONException{
		template.setRequestFactory(new SimpleClientHttpRequestFactory());
		String result = template.getForObject(url, String.class);
		JSONObject jsonObj = new JSONObject(result);
		return jsonObj;
	}
	
}
