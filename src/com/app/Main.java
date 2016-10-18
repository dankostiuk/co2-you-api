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
		
		System.out.println("Daily Summary for last 14 days: ");
		
		List<MovesDay> movesDays = apiService.getLastTwoWeeksDailySummary(accessToken);
		double co2e = processDailyActivities(movesDays);
		
		System.out.println("Total CO2e for last 14 days: " + 
				co2e);
	}
	
	private static double processDailyActivities(List<MovesDay> movesDays) {

		double walkingDistance = 0;
		double cyclingDistance = 0;
		double airplaneDistance = 0;
		double undergroundDistance = 0;
		double tramDistance = 0;
		double busDistance = 0;
		double transportDistance = 0;
		
		
		for (MovesDay movesDay : movesDays) {
			System.out.println(movesDay.getDate() + " : ");
			List<MovesSummary> movesSummaries = movesDay.getSummary();
			
			if (movesSummaries != null && !movesSummaries.isEmpty()) {
				for (MovesSummary movesSummary : movesSummaries) {
					System.out.println("\t" + movesSummary.getActivity() + ":"); 
					System.out.println("\t\tSteps: " + movesSummary.getSteps());
					System.out.println("\t\tDuration: " + movesSummary.getDuration());
					System.out.println("\t\tDistance: " + movesSummary.getDistance());
					
					// activity is walking or running
					if (movesSummary.getSteps() != 0) {
						walkingDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getActivity().equals("cycling")) {
						cyclingDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getActivity().equals("airplane")) {
						airplaneDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getActivity().equals("underground")) {
						airplaneDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getActivity().equals("tram")) {
						tramDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getActivity().equals("bus")) {
						busDistance += movesSummary.getDistance();
					}
					else if (movesSummary.getGroup() != null && 
							movesSummary.getGroup().equals("transport")) {
						transportDistance += movesSummary.getDistance();
					}
				}
			}
		}
		
		// multiple distances in km by coefficients
		return ((walkingDistance/1000) * Constants.WALKING_CO2E_PER_KM) + 
				((cyclingDistance/1000) * Constants.BIKING_CO2E_PER_KM) +
				((airplaneDistance/1000) * Constants.AIR_KG_CO2_PER_PASSENGER_KM) +
				((undergroundDistance/1000) * Constants.SUBWAY_KG_CO2_PER_PASSENGER_KM) +
				((tramDistance/1000) * Constants.STREETCAR_KG_CO2_PER_PASSENGER_KM) +
				((busDistance/1000) * Constants.BUS_KG_CO2_PER_PASSENGER_KM) +
			((transportDistance/1000) * Constants.CAR_KG_CO2_PER_PASSENGER_KM);
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
