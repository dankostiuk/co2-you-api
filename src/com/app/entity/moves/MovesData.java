package com.app.entity.moves;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model representing the MovesData entity.
 * 
 * @author dan
 */
@Entity
@Table(name = "MovesData")
public class MovesData implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="co2_e")
	private double co2E;
	
	@Column
	private String timestamp;
	
	
	public MovesData() {
		
	}
	
	public MovesData(Long id, String userId, double co2E) {
		super();
		this.id = id;
		this.co2E = co2E;
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
	 * @return the co2E
	 */
	public double getCo2E() {
		return co2E;
	}

	/**
	 * @param co2e the co2E to set
	 */
	public void setCo2E(double co2e) {
		co2E = co2e;
	}
	
	/**
	 * @return the timestamp
	 */
	public String getTimeStamp() {
		return timestamp;
	}
}
