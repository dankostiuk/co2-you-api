package com.app.service.moves;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.app.entity.TokenResponse;
import com.app.entity.moves.MovesData;
import com.app.entity.moves.MovesUser;
import com.app.manager.MovesDataManager;
import com.app.manager.MovesUserManager;
import com.app.service.IServiceExecutor;

/**
 * Carries out Moves calculation which involves authentication and a series of
 * Moves API calls.
 * 
 * @author dan
 */
public class MovesServiceExecutor implements IServiceExecutor {

	private final static Logger LOG = Logger.getLogger(MovesServiceExecutor.class);

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

		System.out.println("Starting MovesServiceExecutor...");

		List<MovesUser> movesUsers = _movesUserManager.getAll();
		for (MovesUser movesUser : movesUsers) {
			System.out.println("Carrying out Moves routine for MovesUser with userId " + movesUser.getUserId());
			String accessToken = movesUser.getAccessToken();

			if (accessToken == null) {
				System.out.println(
						"No accessToken detected for MovesUser with userId " + movesUser.getUserId() + ", skipping..");
				continue;
			}

			try {
				if (!_authService.validateAccessToken(accessToken)) {

					System.out.println("AccessToken not valid, attempting refresh of tokens");

					// try refreshing first
					TokenResponse tokenResponse = _authService.refreshTokens(movesUser.getRefreshToken());

					System.out.println("Refresh of tokens success, attempting to save MovesUser with updated tokens.");

					accessToken = tokenResponse.getAccessToken();
					movesUser.setAccessToken(accessToken);
					movesUser.setRefreshToken(tokenResponse.getRefreshToken());
					_movesUserManager.saveMovesUser(movesUser);
				}

				double co2e = _apiService.getDailyCarbon(accessToken);

				System.out.println("Got daily co2e value " + co2e + " for MovesUser with userId " + movesUser.getUserId());

				MovesData movesData = new MovesData();
				movesData.setCo2E(co2e);
				movesData.setUserId(movesUser.getUserId());
				movesData.setTimestamp(new Timestamp(System.currentTimeMillis()));

				_movesDataManager.saveMovesData(movesData);
				
				System.out.println("Saved co2e value and timestamp to MovesUser table for userId " + movesUser.getUserId());

				// increment the total data count for the user
				_movesUserManager.incrementDataRowCount(movesUser.getUserId());
				
				System.out.println("Incremented data_row_count for MovesUser with userId " + movesUser.getUserId());
			} catch (Exception e) {
				System.out.println("Exception occured for userId " + movesUser.getUserId() + ": " + e.getMessage());
			}
		}
	}
}
