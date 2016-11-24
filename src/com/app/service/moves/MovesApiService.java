package com.app.service.moves;

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

import com.app.Constants;
import com.app.entity.moves.MovesDay;
import com.app.entity.moves.MovesDay.MovesSummary;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Carries out Moves API calls.
 * 
 * @author dan
 */
public class MovesApiService {

	public double getDailyCarbon(String accessToken) 
			throws ClientProtocolException, IOException {
		List<MovesDay> movesDays = getDailySummary(accessToken);
		
		return processDailyActivities(movesDays);
	}
	
	private List<MovesDay> getDailySummary(String accessToken) throws ClientProtocolException, IOException {
		
		String uri = "https://api.moves-app.com/api/1.1/user/summary/daily?pastDays=1";
		
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
	
	private double processDailyActivities(List<MovesDay> movesDays) {

		double walkingDistance = 0;
		double cyclingDistance = 0;
		double airplaneDistance = 0;
		double undergroundDistance = 0;
		double tramDistance = 0;
		double busDistance = 0;
		double transportDistance = 0;
		
		for (MovesDay movesDay : movesDays) {
			List<MovesSummary> movesSummaries = movesDay.getSummary();
			
			if (movesSummaries != null && !movesSummaries.isEmpty()) {
				for (MovesSummary movesSummary : movesSummaries) {
					
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
}
