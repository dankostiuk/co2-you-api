package com.app.manager;

import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;

import com.app.entity.moves.MovesData;
import com.app.entity.moves.MovesUser;

/**
 * Carries out MovesData ORM operations.
 * 
 * @author dan
 */
public class MovesDataManager extends AbstractManager<MovesData> {
	
	public MovesDataManager() {
		super(MovesData.class);
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
	 * Gets all movesData.
	 * @return All movesData.
	 */
	public List<MovesData> getAll() {
		List<MovesData> movesData = readAllTransaction();
		return movesData;
	}
	
	/**
	 * Saves the movesData. If the movesData has no id, create the movesData.
	 * Otherwise, update fields from incoming object and persist.
	 * @param movesData The movesData to save.
	 */
	public void saveMovesData(MovesData movesData) {
		
		if (movesData.getId() == null || movesData.getId() == -1) {
			writeTransaction(movesData);
		} else {
			MovesData currentMovesData = readTransaction(movesData.getId());
			
			//TODO: can we even update movesData entries?
			
			writeTransaction(currentMovesData);
		}
	}
}
