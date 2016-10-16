package com.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.app.entity.MovesDay;
import com.app.entity.MovesDay.MovesSummary;

public class Main {
	
	public static final String CLIENT_ID = "d86A71Kz2I5PUC0anuoq67qquUn9Ul4z";
	public static final String CLIENT_SECRET = "Rq7ckF2Z7bTdz1aQ8BQpFCG94z3TjG02C5qL6ExPLU4AKb6kfMlAh1ek0VkPRnJI";
	
	
	public static void main(String[] args) throws IOException {
		
		OAuthService authService = new OAuthService();
		
		try {
			if (!authService.validateAccessToken(loadAccessTokenFromFile())) {
					// try refreshing first
					authService.refreshAccessToken();
					
					// if still cannot validate token, authorize
					if (!authService.validateAccessToken(loadAccessTokenFromFile())) {
						authService.authorize();
					}
			}
		} catch (Exception e) {
			// if an error occured, authorize from scratch
			authService.authorize();				
		}
		
		String accessToken = loadAccessTokenFromFile();
		
		ApiService apiService = new ApiService();
		
		// carry out different api calls -->
		
		System.out.println("Daily Summary for last 7 days: ");
		
		double walkingDuration = 0;
		double totalDuration = 0;
		
		List<MovesDay> movesDays = apiService.getLastWeekDailySummary(accessToken);
		for (MovesDay movesDay : movesDays) {
			System.out.println(movesDay.getDate() + " : ");
			List<MovesSummary> movesSummaries = movesDay.getSummary();
			
			if (movesSummaries != null && !movesSummaries.isEmpty()) {
				for (MovesSummary movesSummary : movesSummaries) {
					System.out.println("\t" + movesSummary.getActivity() + ":"); 
					System.out.println("\t\tSteps: " + movesSummary.getSteps());
					System.out.println("\t\tDuration: " + movesSummary.getDuration());
					System.out.println("\t\tDistance: " + movesSummary.getDistance());
					
					if (movesSummary.getSteps() != 0) {
						walkingDuration += movesSummary.getDistance();
					}
					totalDuration += movesSummary.getDistance();
				}
			}
		}
		
		System.out.println("Total duration spent walking/running vs transport: " 
				+ ((walkingDuration/totalDuration)*100) + "%");
	}
	
	private static String loadAccessTokenFromFile() {
		Properties prop = new Properties();
		InputStream input = null;

		String accessToken;
		try {

			input = new FileInputStream("creds");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			accessToken = prop.getProperty("access_token");
			System.out.println("Using access token: " + accessToken);
			
			return accessToken;
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
}
