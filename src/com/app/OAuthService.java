package com.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.app.entity.TokenResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OAuthService {
	
	public boolean validateAccessToken(String accessToken) throws ClientProtocolException, IOException {
		String uri = "https://api.moves-app.com/oauth/v1/tokeninfo?access_token=" + accessToken;
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		
		HttpResponse response = client.execute(request);
		
		System.out.println("Validate Access Token Response Code : "
                + response.getStatusLine().getStatusCode());

		if (response.getStatusLine().getStatusCode() == 200) {
			return true;
		}
		
		return false;
	}
	
	public void authorize() {
		
		System.out.println("Starting auth process from scratch.. Enter code below in Moves app:");
		
		Map<String, String> tokenMap = null;
	
		try {
			tokenMap = getTokenMap();
		} catch (IOException e1) {
	
		}
		
		System.out.println(tokenMap.get("code"));
		
		
		System.out.println("Press any key to continue...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
		
		String authCode;
		try {
			if (postCheckAuthorized(tokenMap)) {
				authCode = authorizeAndRedirect(tokenMap);
				System.out.println("Auth code: " + authCode);
				
				String response = getAccessToken(authCode, tokenMap);
				Gson gson = new GsonBuilder()
					    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
					    .create();
				TokenResponse tokenResponse = gson.fromJson(response, TokenResponse.class);
				
				saveTokenResponseToFile(tokenResponse);
			}
			
		} catch (IOException e) {

		}
	}
	
	private Map<String, String> getTokenMap() throws ClientProtocolException, IOException {
		String uri = "https://api.moves-app.com/oauth/v1/authorize?response_type=code&client_id=" 
				+ Main.CLIENT_ID + "&scope=activity";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		
		HttpResponse response = client.execute(request);
		
		Header cookieHeader = response.getAllHeaders()[response.getAllHeaders().length-1];
		HeaderElement element = cookieHeader.getElements()[0];
		String playSessionCookie = element.getName() + "=" + element.getValue();
		String cookie = "Cookie:__utmt=1; "
				+ "__utma=6285795.1831779793.1475246271.1475246271.1475246271.1; "
				+ "__utmb=6285795.2.10.1475246271; "
				+ "__utmc=6285795; "
				+ "__utmz=6285795.1475246271.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);"
				+ playSessionCookie;
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		Document doc = Jsoup.parse(result.toString());
		Elements divs = doc.getElementsByClass("digitgroup");
		
		Element requestCodeElement = doc.select("input[name=request_code]").first();
		String requestCode = requestCodeElement.attr("value");
		
		Element authTokenElement = doc.select("input[name=auth_token]").first();
		String authToken = authTokenElement.attr("value");
		
		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("code", divs.get(0).ownText() + " " + divs.get(1).ownText());
		tokenMap.put("authToken", authToken);
		tokenMap.put("requestCode", requestCode);
		tokenMap.put("cookie", cookie);
		
		return tokenMap;
	}
	
	private boolean postCheckAuthorized(Map<String, String> tokenMap) throws ClientProtocolException, IOException {
		String uri = "https://api.moves-app.com/oauth/v1/checkAuthorized";
		
		String authToken = tokenMap.get("authToken");
		String requestCode = tokenMap.get("requestCode");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(uri);
		
		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Cookie", tokenMap.get("cookie"));

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("auth_token", authToken));
		urlParameters.add(new BasicNameValuePair("request_code", requestCode));
		urlParameters.add(new BasicNameValuePair("client_id", Main.CLIENT_ID));

		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpResponse response = client.execute(post);
		System.out.println("Check Authorized Response Code : "
		                + response.getStatusLine().getStatusCode());
		
		if (response.getStatusLine().getStatusCode() == 200) {
			return true;
		}
		
		return false;
	}
	
	private String authorizeAndRedirect(Map<String, String> tokenMap) throws UnsupportedOperationException, IOException {
		
		String uri = "https://api.moves-app.com/oauth/v1/authorizeAndRedirect";
		
		String authToken = tokenMap.get("authToken");
		String requestCode = tokenMap.get("requestCode");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(uri);
		
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Cookie", tokenMap.get("cookie"));

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("auth_token", authToken));
		urlParameters.add(new BasicNameValuePair("request_code", requestCode));
		urlParameters.add(new BasicNameValuePair("response_type", "code"));
		urlParameters.add(new BasicNameValuePair("client_id", Main.CLIENT_ID));
		urlParameters.add(new BasicNameValuePair("redirect_uri", ""));
		urlParameters.add(new BasicNameValuePair("scope", "activity"));
		urlParameters.add(new BasicNameValuePair("state", ""));
		urlParameters.add(new BasicNameValuePair("error_uri", "www.google.com"));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpResponse response = client.execute(post);
		System.out.println("Authorize and Redirect Response Code : "
		                + response.getStatusLine().getStatusCode());

		Header locationHeader = response.getAllHeaders()[4];
		String location = locationHeader.getValue();
		
		return location.split("=")[1].split("&")[0];
		
	}
	
	private String getAccessToken(String authCode, Map<String, String> tokenMap) throws ClientProtocolException, IOException {
		
		String uri = "https://api.moves-app.com/oauth/v1/access_token";
		
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(uri);
		
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Cookie", tokenMap.get("cookie"));
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
		urlParameters.add(new BasicNameValuePair("code", authCode));
		urlParameters.add(new BasicNameValuePair("client_id", Main.CLIENT_ID));
		urlParameters.add(new BasicNameValuePair("client_secret", Main.CLIENT_SECRET));
		urlParameters.add(new BasicNameValuePair("redirect_uri", ""));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpResponse response = client.execute(post);
		System.out.println("Access Token Response Code : "
		                + response.getStatusLine().getStatusCode());
		
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return result.toString();
	}
	
	public void refreshAccessToken() throws ClientProtocolException, IOException {
		
		String refreshToken = loadRefreshTokenFromFile();
		
		String uri = "https://api.moves-app.com/oauth/v1/access_token?"
				+ "grant_type=refresh_token&"
				+ "refresh_token=" + refreshToken + "&"
				+ "client_id=" + Main.CLIENT_ID + "&"
				+ "client_secret=" + Main.CLIENT_SECRET;

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(uri);
		
		HttpResponse response = client.execute(post);
		

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
		
		TokenResponse tokenResponse = gson.fromJson(result.toString(), TokenResponse.class);
		saveTokenResponseToFile(tokenResponse);
	}
	
	private static String loadRefreshTokenFromFile() {
		Properties prop = new Properties();
		InputStream input = null;

		String refreshToken;
		try {

			input = new FileInputStream("creds");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			refreshToken = prop.getProperty("refresh_token");
			System.out.println("Using refresh token: " + refreshToken);
			
			return refreshToken;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private void saveTokenResponseToFile(TokenResponse tokenResponse) {
		// save to creds file
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream("creds");

			// set the properties value
			prop.setProperty("access_token", tokenResponse.getAccessToken());
			prop.setProperty("token_type", tokenResponse.getTokenType());
			prop.setProperty("expires_in", tokenResponse.getExpiresIn());
			prop.setProperty("refresh_token", tokenResponse.getRefreshToken());
			prop.setProperty("user_id", tokenResponse.getUserId());

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
