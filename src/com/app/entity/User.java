package com.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Table;

/**
 * Model representing the User entity.
 * 
 * @author dan
 */
@Entity
@Table(indexes = {}, appliesTo = "User")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="oauth_access_token")
	private String oauthAccessToken;
	
	@Column(name="oauth_refresh_token")
	private String oauthRefreshToken;
	
	public User() {
		
	}
	
	public User(Long id, String userId, String oathAccessToken, String oathRefreshToken) {
		super();
		this.id = id;
		this.userId = userId;
		this.oauthAccessToken = oathAccessToken;
		this.oauthRefreshToken = oathRefreshToken;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the oauthAccessToken
	 */
	public String getOauthAccessToken() {
		return oauthAccessToken;
	}

	/**
	 * @param oauthAccessToken the oauthAccessToken to set
	 */
	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}

	/**
	 * @return the oauthRefreshToken
	 */
	public String getOauthRefreshToken() {
		return oauthRefreshToken;
	}

	/**
	 * @param oauthRefreshToken the oathRefreshToken to set
	 */
	public void setOauthRefreshToken(String oauthRefreshToken) {
		this.oauthRefreshToken = oauthRefreshToken;
	}
}
