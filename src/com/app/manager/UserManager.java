package com.app.manager;

import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;

import com.app.entity.User;

/**
 * Carries out User ORM operations.
 * 
 * @author dan
 */
public class UserManager extends AbstractManager<User> {
	
	public UserManager() {
		super(User.class);
	}
	
	/**
	 * Get the user for the given id.
	 * @param id The id of the user to find.
	 * @return The user to find.
	 * @throws NamingException If an error occurs.
	 */
	public User getUser(int id) throws NamingException {
		User user = readTransaction(id);
		
		return user;
	}
	
	/**
	 * Find user by userId
	 * @param userId The userId of the user to find.
	 * @return The user that matches the given userId.
	 * @throws EntityNotFoundException If an error occurs.
	 */
	public User findUser(String userId) throws EntityNotFoundException {
		User user = findTransaction("user_id", userId);
		
		return user;
	}
	
	/**
	 * Gets all users.
	 * @return All users.
	 */
	public List<User> getAll() {
		List<User> users = readAllTransaction();
		return users;
	}
	
	/**
	 * Saves the user. If the user has no id, create the user.
	 * Otherwise, update fields from incoming object and persist.
	 * @param user The user to save.
	 */
	public void saveUser(User user) {
		
		if (user.getId() == null || user.getId() == -1) {
			writeTransaction(user);
		} else {
			User currentUser = readTransaction(user.getId());
			
			if (user.getOauthAccessToken() != null) {
				currentUser.setOauthAccessToken(user.getOauthAccessToken());
			}
			if (user.getOauthRefreshToken() != null) {
				currentUser.setOauthRefreshToken(user.getOauthRefreshToken());
			}
			
			writeTransaction(currentUser);
		}
		
	}
}
