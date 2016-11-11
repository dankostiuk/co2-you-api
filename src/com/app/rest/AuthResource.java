package com.app.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.app.Constants;
import com.app.entity.SummaryResponse;
import com.app.entity.SummaryResponse.SummaryType;
import com.app.entity.TokenResponse;
import com.app.entity.User;
import com.app.entity.moves.MovesData;
import com.app.entity.moves.MovesUser;
import com.app.manager.MovesDataManager;
import com.app.manager.MovesUserManager;
import com.app.manager.UserManager;
import com.app.service.moves.MovesOAuthService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/auth")
public class AuthResource {
	
	UserManager _userManager = new UserManager();
	MovesUserManager _movesUserManager = new MovesUserManager(_userManager.getEntityManagerFactory());
	MovesDataManager _movesDataManager = new MovesDataManager(_userManager.getEntityManagerFactory());
	
	/**
	 * Process Auth0 callback
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryResponse processCallback(@QueryParam("code") String code) 
			throws ClientProtocolException, IOException {
		
		if (code == null)
		{
			return new SummaryResponse(400, null, "No 'code' param specified", SummaryType.ERROR);
		}
		
		HttpClient client = HttpClientBuilder.create().build();
		String uri = "https://app58285542.auth0.com/oauth/token";
		HttpPost post = new HttpPost(uri);
		
		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("client_id", Constants.AUTH0_CLIENT_ID));
		urlParameters.add(new BasicNameValuePair("client_secret", Constants.AUTH0_CLIENT_SECRET));
		urlParameters.add(new BasicNameValuePair("redirect_uri", "http://app.co2-you.com/home"));
		urlParameters.add(new BasicNameValuePair("code", code));
		urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));

		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpResponse response = client.execute(post);
		
		String result = responseToString(response);
		
		Gson gson = new GsonBuilder()
			    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			    .create();
		TokenResponse tokenResponse = gson.fromJson(result.toString(), TokenResponse.class);
		
		// get access token
		String accessToken = tokenResponse.getAccessToken();

		String getUri = "https://app58285542.auth0.com/userinfo/?access_token=" + accessToken;
		HttpGet get = new HttpGet(getUri);
		
		HttpResponse getResponse = client.execute(get);
		
		String auth0UserInfo = responseToString(getResponse);
		
		//TODO: analyze all 3 social logins + manual login and figure out
		// what kind of userId to persist to user table.
		// next, check if userId already has a moves access_token, if not, prompt for Moves PIN
			// on submit of Moves PIN, display 2 week co2e
		// if userId has moves access_token, display latest 2 week co2e
		
		SummaryResponse summaryResponse = processUsername(auth0UserInfo, tokenResponse);
		return summaryResponse;
	}
	
	/*
	 * TODO: spit this into manageable methods
	 * 
	 */
	private SummaryResponse processUsername(String auth0UserInfo, TokenResponse tokenResponse) 
			throws JsonParseException, JsonMappingException, IOException {
		
		MovesOAuthService movesAuthService = new MovesOAuthService();
		Map<String, String> tokenMap = movesAuthService.getTokenMap();
		
		// get the user_id from userInfo returned from auth0
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode array = objectMapper.readValue(auth0UserInfo, JsonNode.class);
		String userId = array.get("user_id").textValue();
		String name = array.get("given_name") == null ? 
				array.get("name").textValue() : array.get("given_name").textValue();
		
		// save if user doesn't exist in db, otherwise continue
		User user = _userManager.findUser(userId);
		if (user == null) {
			User newUser = new User();
			newUser.setUserId(userId);
			newUser.setOauthAccessToken(tokenResponse.getAccessToken());
			newUser.setName(name);
			_userManager.saveUser(newUser);
			
			// user saved, send back status to tell frontend
			return new SummaryResponse(200, name,
					"Please enter 8 digit PIN '" + 
							tokenMap.get("code") + "' into Moves app and press Submit.", SummaryType.REGISTER);
		} else {
			
			// check if user authenticated with Moves
			
			MovesUser movesUser = _movesUserManager.findMovesUserByUserId(userId);
			
			String movesAccessToken = "";
			if (movesUser == null) {
				return new SummaryResponse(200, name,
						"Please enter 8 digit PIN '" + 
								tokenMap.get("code") + "' into Moves app and press Submit.", SummaryType.REGISTER);
			} else {
				movesAccessToken = movesUser.getAccessToken();
				if (movesAccessToken == null) {
					// user exists but has not connected to moves, send back status
					// to tell frontend to prompt for 5-digit Moves PIN
					return new SummaryResponse(200, name,
							"Please enter 8 digit PIN '" + 
									tokenMap.get("code") + "' into Moves app and press Submit.", SummaryType.REGISTER);
				}
			}
			
			// Moves accessToken exists, display latest value
			List<MovesData> movesDataList = _movesDataManager.getAll();

			return new SummaryResponse(200, name,
					String.valueOf(movesDataList.get(0).getCo2E()), SummaryType.INFO);
		}
	}
	
	private String responseToString(HttpResponse response) 
			throws UnsupportedOperationException, IOException {
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
