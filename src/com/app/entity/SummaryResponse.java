package com.app.entity;

/**
 * Summary response object.
 * 
 * @author dan
 */
public class SummaryResponse {
	public String message;
	
	public String name;
	
	public int statusCode;
	
	public int summaryType;
	
	public enum SummaryType {
		ERROR(0), 
		INFO(1), 
		REGISTER(2);
		
		private final int summaryTypeCode;
		
        SummaryType(int summaryTypeCode) {
        	this.summaryTypeCode = summaryTypeCode;
        }
        
        public int getSummaryTypeCode() {
            return this.summaryTypeCode;
        }
	};
	
	public SummaryResponse(int statusCode, String name, String message, SummaryType summaryType) {
		this.statusCode = statusCode;
		this.name = name;
		this.message = message;
		this.summaryType = summaryType.getSummaryTypeCode();
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the summaryType
	 */
	public int getSummaryType() {
		return summaryType;
	}

	/**
	 * @param summaryType the summaryType to set
	 */
	public void setSummaryType(int summaryType) {
		this.summaryType = summaryType;
	}
}
