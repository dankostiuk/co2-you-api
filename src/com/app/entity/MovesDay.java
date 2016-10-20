package com.app.entity;

import java.util.List;

/**
 * Moves API Day, as returned by API DailySummary call.
 * 
 * @author dan
 */
public class MovesDay {
	private int date;
	private List<MovesSummary> summary;
	private String lastUpdate;

	/**
	 * @return the date
	 */
	public int getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(int date) {
		this.date = date;
	}

	/**
	 * @return the summary
	 */
	public List<MovesSummary> getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(List<MovesSummary> summary) {
		this.summary = summary;
	}

	/**
	 * @return the lastUpdate
	 */
	public String getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public class MovesSummary {
		private String activity;
		private String group;
		private int duration;
		private int distance;
		private int steps;
		
		/**
		 * @return the activity
		 */
		public String getActivity() {
			return activity;
		}
		/**
		 * @param activity the activity to set
		 */
		public void setActivity(String activity) {
			this.activity = activity;
		}
		/**
		 * @return the group
		 */
		public String getGroup() {
			return group;
		}
		/**
		 * @param group the group to set
		 */
		public void setGroup(String group) {
			this.group = group;
		}
		/**
		 * @return the duration
		 */
		public int getDuration() {
			return duration;
		}
		/**
		 * @param duration the duration to set
		 */
		public void setDuration(int duration) {
			this.duration = duration;
		}
		/**
		 * @return the distance
		 */
		public int getDistance() {
			return distance;
		}
		/**
		 * @param distance the distance to set
		 */
		public void setDistance(int distance) {
			this.distance = distance;
		}
		/**
		 * @return the steps
		 */
		public int getSteps() {
			return steps;
		}
		/**
		 * @param steps the steps to set
		 */
		public void setSteps(int steps) {
			this.steps = steps;
		}
	}
}
