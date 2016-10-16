package com.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.app.entity.MovesDay;
import com.app.entity.TokenResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ApiService {

	public List<MovesDay> getLastWeekDailySummary(String accessToken) throws ClientProtocolException, IOException {
		
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
		
		Gson gson = new GsonBuilder()
			    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			    .create();
		return gson.fromJson(result.toString(), new TypeToken<ArrayList<MovesDay>>(){}.getType());
	}
}
