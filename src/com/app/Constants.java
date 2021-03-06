package com.app;

/**
 * Maintained list of constants.
 * 
 * @author dan
 */
public class Constants {
	
	// ServletContext cache key
	public static final String CACHE_KEY = "cache";
	
	// -- Auth0 API KEys
	public static final String AUTH0_CLIENT_ID = "TJSO4rVtfO9NehWy3YrmSWXNQEXIqZQK";
	public static final String AUTH0_CLIENT_SECRET = System.getenv("AUTH0_CLIENT_SECRET");
	
	// -- Moves API Keys
	public static final String MOVES_CLIENT_ID = "d86A71Kz2I5PUC0anuoq67qquUn9Ul4z";
	public static final String MOVES_CLIENT_SECRET = System.getenv("MOVES_CLIENT_SECRET");
	
	
	// -- Moves API CO2e coefficients
	public static final double WALKING_CO2E_PER_KM = 0.016;
	public static final double BIKING_CO2E_PER_KM = 0.013;
	
	public static final double CAR_KG_CO2_PER_PASSENGER_KM = 0.251;
	
	public static final double AIR_KG_CO2_PER_PASSENGER_KM = 0.124;
	public static final double SUBWAY_KG_CO2_PER_PASSENGER_KM = 0.075;
	public static final double STREETCAR_KG_CO2_PER_PASSENGER_KM = 0.025;
	public static final double BUS_KG_CO2_PER_PASSENGER_KM = 0.0341;
}
