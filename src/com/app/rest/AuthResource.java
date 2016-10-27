package com.app.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.app.entity.TokenResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/auth")
public class AuthResource {
	
	/**
	 * Process Auth0 callback
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response processCallback(@QueryParam("code") String code) throws ClientProtocolException, IOException {
		if (code == null)
		{
			return Response.status(400).build();
		}
		
		HttpClient client = HttpClientBuilder.create().build();
		String uri = "https://app58285542.auth0.com/oauth/token";
		HttpPost post = new HttpPost(uri);
		
		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("client_id", Constants.AUTH0_CLIENT_ID));
		urlParameters.add(new BasicNameValuePair("client_secret", Constants.AUTH0_CLIENT_SECRET));
		urlParameters.add(new BasicNameValuePair("redirect_uri", "https://co2-you.herokuapp.com/callback"));
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
		
		String getResponseString = responseToString(getResponse);
		
		return Response.status(200).entity(getResponseString).build();
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
