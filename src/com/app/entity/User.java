package com.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model representing the User entity.
 * 
 * @author dan
 */
@Entity
@Table(name = "User")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String username;
	
	@Column(name="access_token")
	private String accessToken;
	
	@Column(name="facebook_id")
	private String facebookId;
	
	@Column(name="google_id")
	private String googleId;
	
	@Column(name="linkedin_id")
	private String linkedInId;
	
	public User() {
		
	}
	
	public User(Long id, String username, String accessToken, String facebookId, 
			String googleId, String linkedInId) {
		super();
		this.id = id;
		this.username = username;
		this.accessToken = accessToken;
		this.facebookId = facebookId;
		this.googleId = googleId;
		this.linkedInId = linkedInId;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the facebookId
	 */
	public String getFacebookId() {
		return facebookId;
	}

	/**
	 * @param facebookId the facebookId to set
	 */
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	/**
	 * @return the googleId
	 */
	public String getGoogleId() {
		return googleId;
	}

	/**
	 * @param googleId the googleId to set
	 */
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	/**
	 * @return the linkedInId
	 */
	public String getLinkedInId() {
		return linkedInId;
	}

	/**
	 * @param linkedInId the linkedInId to set
	 */
	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}
	
	
}
