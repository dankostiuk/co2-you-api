package com.app.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.app.FileReaderWriter;
import com.app.service.moves.MovesApiService;
import com.app.service.moves.MovesOAuthService;

/**
 * Carries out calls to different services (i.e. external APIs).
 * 
 * @author dan
 */
public class ServiceExecutor {
	
	public void executeServices() {
		
		// execute Moves auth + api calls
		executeMoves();
	}
	
	/**
	 * Carry out Moves auth and api calls.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void executeMoves() {
		MovesOAuthService authService = new MovesOAuthService();
		
		try {
			if (!authService.validateAccessToken(FileReaderWriter.loadAccessTokenFromFile())) {
					// try refreshing first
					authService.refreshAccessToken();
					
					// if still cannot validate token, authorize
					if (!authService.validateAccessToken(FileReaderWriter.loadAccessTokenFromFile())) {
						authService.authorize();
					}
			}
		} catch (Exception e) {
			// if an error occured, authorize from scratch
			authService.authorize();				
		}
		
		String accessToken = FileReaderWriter.loadAccessTokenFromFile();
		
		MovesApiService apiService = new MovesApiService();
	
		try {
			double co2e = apiService.getLastTwoWeeksCarbon(accessToken);
			
			System.out.println("Total CO2e for last 14 days: " + 
					co2e);
		} catch (IOException ioe) {
			//TODO: do something with this exception
		}
	}
}
