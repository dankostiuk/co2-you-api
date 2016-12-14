package com.app.manager;

import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;

import com.app.entity.moves.MovesUser;

/**
 * Carries out MovesUser ORM operations.
 * 
 * @author dan
 */
public class MovesUserManager extends AbstractManager<MovesUser> {

	public MovesUserManager() {
		super(MovesUser.class);
	}

	public MovesUserManager(EntityManagerFactory emf) {
		super(MovesUser.class, emf);
	}

	/**
	 * Get the movesUser for the given id.
	 * 
	 * @param id
	 *            The id of the movesUser to find.
	 * @return The movesUser to find.
	 * @throws NamingException
	 *             If an error occurs.
	 */
	public MovesUser getMovesUser(int id) throws NamingException {
		MovesUser movesUser = readTransaction(id);

		return movesUser;
	}

	/**
	 * Find movesUser by userId
	 * 
	 * @param userId
	 *            The userId of the movesUser to find.
	 * @return The movesUser that matches the given userId.
	 * @throws EntityNotFoundException
	 *             If an error occurs.
	 */
	public MovesUser findMovesUserByUserId(String userId) throws EntityNotFoundException {
		MovesUser movesUser = findTransaction("userId", userId);

		return movesUser;
	}

	/**
	 * Gets all movesUsers.
	 * 
	 * @return All movesUsers.
	 */
	public List<MovesUser> getAll() {
		List<MovesUser> movesUsers = readAllTransaction();
		return movesUsers;
	}

	/**
	 * Saves the movesUser. If the movesUser has no id, create the movesUser.
	 * Otherwise, update fields from incoming object and persist.
	 * 
	 * @param movesUser
	 *            The movesUser to save.
	 */
	public void saveMovesUser(MovesUser movesUser) {

		if (movesUser.getId() == null || movesUser.getId() == -1) {
			writeTransaction(movesUser);
		} else {
			MovesUser currentMovesUser = readTransaction(movesUser.getId());

			if (movesUser.getAccessToken() != null) {
				currentMovesUser.setAccessToken(movesUser.getAccessToken());
			}
			if (movesUser.getRefreshToken() != null) {
				currentMovesUser.setRefreshToken(movesUser.getRefreshToken());
			}

			writeTransaction(currentMovesUser);
		}
	}

	/**
	 * Increments the MovesData row count for the MovesUser id.
	 * 
	 * @param movesUserId
	 *            The id of the MovesUser.
	 */
	public void incrementDataRowCount(String movesUserId) {
		String query = "update MovesUser "
				+ "set data_row_count = data_row_count + 1 "
				+ "where user_id='" + movesUserId + "'";
		
		updateByNativeQuery(query);
	}
}
