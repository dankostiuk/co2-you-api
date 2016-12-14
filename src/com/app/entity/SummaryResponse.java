package com.app.entity;

import java.util.List;

import com.app.entity.moves.MovesData;

/**
 * Summary response object.
 * 
 * @author dan
 */
public class SummaryResponse {
	public String message;

	public List<MovesData> movesData;
	public double movesDailyAverageCo2e;

	public String name;

	public String userId;

	public int statusCode;

	public int summaryType;

	public enum SummaryType {
		ERROR(0), INFO(1), REGISTER(2);

		private final int summaryTypeCode;

		SummaryType(int summaryTypeCode) {
			this.summaryTypeCode = summaryTypeCode;
		}

		public int getSummaryTypeCode() {
			return this.summaryTypeCode;
		}
	};

	public SummaryResponse(int statusCode, String name, String userId, String message, List<MovesData> movesData,
			double movesDailyAverageCo2e, SummaryType summaryType) {
		this.statusCode = statusCode;
		this.name = name;
		this.userId = userId;
		this.message = message;
		this.movesData = movesData;
		this.movesDailyAverageCo2e = movesDailyAverageCo2e;
		this.summaryType = summaryType.getSummaryTypeCode();
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the movesData
	 */
	public List<MovesData> getMovesData() {
		return movesData;
	}

	/**
	 * @param movesData
	 *            the movesData to set
	 */
	public void setMovesData(List<MovesData> movesData) {
		this.movesData = movesData;
	}
	
	/**
	 * @return the movesDailyAverageCo2e
	 */
	public double getMovesDailyAverageCo2e() {
		return movesDailyAverageCo2e;
	}

	/**
	 * @param movesDailyAverageCo2e the movesDailyAverageCo2e to set
	 */
	public void setMovesDailyAverageCo2e(double movesDailyAverageCo2e) {
		this.movesDailyAverageCo2e = movesDailyAverageCo2e;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the summaryType
	 */
	public int getSummaryType() {
		return summaryType;
	}

	/**
	 * @param summaryType
	 *            the summaryType to set
	 */
	public void setSummaryType(int summaryType) {
		this.summaryType = summaryType;
	}
}
