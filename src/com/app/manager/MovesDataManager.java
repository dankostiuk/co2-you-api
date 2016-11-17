package com.app.manager;

import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import com.app.entity.moves.MovesData;

/**
 * Carries out MovesData ORM operations.
 * 
 * @author dan
 */
public class MovesDataManager extends AbstractManager<MovesData> {
	
	public MovesDataManager() {
		super(MovesData.class);
	}
	
	public MovesDataManager(EntityManagerFactory emf) {
		super(MovesData.class, emf);
	}
	
	/**
	 * Get the movesData for the given id.
	 * @param id The id of the movesData to find.
	 * @return The movsData to find.
	 * @throws NamingException If an error occurs.
	 */
	public MovesData getMovesData(int id) throws NamingException {
		MovesData movesData = readTransaction(id);
		
		return movesData;
	}
	
	/**
	 * Gets the latest MovesData for the specified userId
	 * @param userId The user id to obtain the co2e value for.
	 * @return The latest MovesData entry.
	 */
	public MovesData findLatestMovesDataUserId(String userId) {
		
		String query = 
			"select * from MovesData "
				+ "where user_id='" + userId + "' "
				+ "and "
				+ "timestamp = (SELECT MAX(timestamp) FROM MovesData "
				+ "		where user_id='" + userId + "');";
		
		List<MovesData> movesDataList = runNativeQuery(query);
		return movesDataList.get(0);
	}

	/**
	 * Gets all movesData.
	 * @return All movesData.
	 */
	public List<MovesData> getAll() {
		List<MovesData> movesData = readAllTransaction();
		return movesData;
	}
	
	/**
	 * Saves the movesData. Note that we cannot modify existing entries.
	 * @param movesData The movesData to save.
	 */
	public void saveMovesData(MovesData movesData) {
		writeTransaction(movesData);
	}
}
