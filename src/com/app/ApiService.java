package com.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApiService {

	public String getLastWeekDailySummary(String accessToken) throws ClientProtocolException, IOException {
		
		String uri = "https://api.moves-app.com/api/1.1/user/summary/daily?pastDays=7";
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(uri);
		
		get.setHeader("Content-Type", "application/json");
		get.setHeader("Authorization", "Bearer " + accessToken);
		
		HttpResponse response = client.execute(get);
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return result.toString();
	}
}
