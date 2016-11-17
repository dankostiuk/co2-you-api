package com.app.service.moves;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.app.entity.TokenResponse;
import com.app.entity.moves.MovesData;
import com.app.entity.moves.MovesUser;
import com.app.manager.MovesDataManager;
import com.app.manager.MovesUserManager;
import com.app.service.IServiceExecutor;

/**
 * Carries out Moves calculation which involves authentication and a series of Moves 
 * API calls. 
 * 
 * @author dan
 */
public class MovesServiceExecutor implements IServiceExecutor {
	
	private MovesUserManager _movesUserManager;
	private MovesDataManager _movesDataManager;
	private MovesOAuthService _authService;
	private MovesApiService _apiService;
	
	public MovesServiceExecutor() {
		_movesUserManager = new MovesUserManager();
		_movesDataManager = new MovesDataManager();
		_authService = new MovesOAuthService();
		_apiService = new MovesApiService();
	}
	
	/**
	 * Carry out Moves auth and api calls.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Override
	public void execute() {
		
		List<MovesUser> movesUsers = _movesUserManager.getAll();
		for (MovesUser movesUser : movesUsers) {
			String accessToken = movesUser.getAccessToken();
			
			if (accessToken == null) {
				continue;
			}
			
			try {
				if (!_authService.validateAccessToken(accessToken)) {
					// try refreshing first
					TokenResponse tokenResponse = _authService.refreshTokens(movesUser.getRefreshToken());
					
					accessToken = tokenResponse.getAccessToken();
					movesUser.setAccessToken(accessToken);
					movesUser.setRefreshToken(tokenResponse.getRefreshToken());
					_movesUserManager.saveMovesUser(movesUser);
				}
				
				double co2e = _apiService.getLastTwoWeeksCarbon(accessToken);
				
				MovesData movesData = new MovesData();
				movesData.setCo2E(co2e);
				movesData.setUserId(movesUser.getUserId());
				
				_movesDataManager.saveMovesData(movesData);
			} catch (Exception e) {
				System.out.println("Got exception: " + e.getMessage());
			}
		}
	}
}
