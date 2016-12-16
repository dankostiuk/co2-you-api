package com.app.manager;

import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.app.entity.moves.MovesData;

/**
 * Carries out MovesData ORM operations.
 * 
 * @author dan
 */
public class MovesDataManager extends AbstractManager<MovesData> {

	private final static Logger LOG = Logger.getLogger(MovesDataManager.class);

	public MovesDataManager() {
		super(MovesData.class);
	}

	public MovesDataManager(EntityManagerFactory emf) {
		super(MovesData.class, emf);
	}

	/**
	 * Get the movesData for the given id.
	 * 
	 * @param id
	 *            The id of the movesData to find.
	 * @return The movsData to find.
	 * @throws NamingException
	 *             If an error occurs.
	 */
	public MovesData getMovesData(int id) throws NamingException {
		MovesData movesData = readTransaction(id);

		return movesData;
	}

	/**
	 * Gets the latest 7 days of MovesData for the specified userId
	 * 
	 * @param userId
	 *            The user id to obtain the MovesData for.
	 * @return List of latest 7 MovesData entries.
	 */
	public List<MovesData> findLastSevenDaysMovesDataUserId(String userId) {

		LOG.debug("Attempting to get last 7 days of MovesData for userId " + userId);

		DateTime dt = new DateTime();
		DateTime sevenDaysAgo = dt.minusDays(7);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		String dtStr = fmt.print(sevenDaysAgo);

		String query = "select * from MovesData " + "where user_id='" + userId + "' " + "and "
				+ "(is_avg is null or is_avg=b'0')" + "and " + "timestamp between '" + dtStr
				+ "' and NOW() order by timestamp";

		List<MovesData> movesDataList = getListByNativeQuery(query);
		return movesDataList;
	}

	/**
	 * Gets the Moves daily average co2e for the specified userId
	 * 
	 * @param userId
	 *            The user id to obtain the co2e value for.
	 * @return The average daily co2e.
	 */
	public MovesData getDailyAverageForUserId(String userId) {

		LOG.debug("Attempting to get dailyAverage for userId " + userId);

		String query = "select * from MovesData where user_id='" + userId + "' and is_avg=b'1'";

		List<MovesData> movesData = getListByNativeQuery(query);
		return movesData.get(0);
	}

	/**
	 * Gets the MovesData row count for the given userId.
	 * 
	 * @param userId
	 *            The user id to obtain the Moves data row count for.
	 * @return An integer representing the number of MovesData rows.
	 */
	public Integer getDataRowCountForUserId(String userId) {

		String query = "select data_row_count from MovesUser " + "where user_id='" + userId + "'";

		Integer dataRowCount = getCountByNativeQuery(query);
		LOG.debug("Got data_row_count " + dataRowCount + " for userId " + userId);

		return dataRowCount;
	}

	/**
	 * Gets all movesData.
	 * 
	 * @return All movesData.
	 */
	public List<MovesData> getAll() {
		List<MovesData> movesData = readAllTransaction();
		return movesData;
	}

	/**
	 * Saves the movesData and updates the average. Note that we cannot modify
	 * existing entries.
	 * 
	 * @param movesData
	 *            The movesData to save.
	 */
	public void saveMovesData(MovesData movesData) {

		// save the incoming data
		writeTransaction(movesData);

		LOG.debug("Saved incoming MovesData for userId " + movesData.getUserId());

		updateDailyAverage(movesData);
	}

	/**
	 * Updates the MovesData daily average by getting the average of current
	 * co2e plus incoming movesData co2e divided by the total row count for the
	 * movesData user.
	 * 
	 * @param movesData
	 *            The incoming movesData that has been saved.
	 */
	private void updateDailyAverage(MovesData movesData) {

		LOG.debug("Attempting to update dailyAverage for userId " + movesData.getUserId());

		MovesData dailyAverage = getDailyAverageForUserId(movesData.getUserId());
		if (dailyAverage == null) {
			LOG.debug("Current dailyAverage not set, creating new dailyAverage for user " + movesData.getUserId());
			dailyAverage = new MovesData();
			dailyAverage.setAverage(true);
			dailyAverage.setUserId(movesData.getUserId());
			dailyAverage.setCo2E(movesData.getCo2E());
		} else {
			LOG.debug("Current dailyAverage set, finding data_row_count for user " + movesData.getUserId());
			double newAverage;
			int movesDataRowCount = getDataRowCountForUserId(movesData.getUserId());
			newAverage = (dailyAverage.getCo2E() + movesData.getCo2E()) / movesDataRowCount;
			dailyAverage.setCo2E(newAverage);
		}

		LOG.debug("Attempting to save new dailyAverage with co2e '" + dailyAverage.getCo2E() + "' for userId "
				+ movesData.getUserId());

		// try to save a new average, if it exists, perform a native update
		try {
			writeTransaction(dailyAverage);
		} catch (EntityExistsException eee) {
			
			LOG.debug("MovesData dailyAverage already exists for userId "
					+ movesData.getUserId() + ". Trying to update...", eee);
			
			String query = "update MovesData " + "set co2_e=" + dailyAverage.getCo2E() + "where id='"
					+ dailyAverage.getId() + "'";
			updateByNativeQuery(query);
			
			LOG.debug("Updated dailyAverage for userId " + movesData.getUserId());
		}
	}
}
