package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * This class represents Publication to MQ topic from particular client (connectionId/activeConnection).
 *
 */
public class Publication {

	/**
	 * For explanation of all topic attributes see IBM documentation:
	 * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088150_.htm
	 */
	private TopicString topicString;
	private String lastPublishDate;
	private String lastPublishTime;
	private int numberOfPublishes; 
	private String activeConnection;
	
	
	public Publication(TopicString topicString, String lastPublishDate, String lastPublishTime,
			int numberOfPublishes, String activeConnection) {
		super();
		this.topicString = topicString;
		this.lastPublishDate = lastPublishDate;
		this.lastPublishTime = lastPublishTime;
		this.numberOfPublishes = numberOfPublishes;
		this.activeConnection = activeConnection;
	}


	public TopicString getTopicString() {
		return topicString;
	}


	public String getLastPublishDate() {
		return lastPublishDate;
	}


	public String getLastPublishTime() {
		return lastPublishTime;
	}


	public int getNumberOfPublishes() {
		return numberOfPublishes;
	}

	
	public void setNumberOfPublishes(int numberOfPublishes) {
		this.numberOfPublishes = numberOfPublishes;
	}


	public String getActiveConnection() {
		return activeConnection;
	}


	@Override
	public String toString() {
		return "Publication [topicString=" + topicString + ", lastPublishDate="
				+ lastPublishDate + ", lastPublishTime=" + lastPublishTime
				+ ", numberOfPublishes=" + numberOfPublishes
				+ ", activeConnection=" + activeConnection + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((activeConnection == null) ? 0 : activeConnection.hashCode());
		result = prime * result
				+ ((topicString == null) ? 0 : topicString.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Publication other = (Publication) obj;
		if (activeConnection == null) {
			if (other.activeConnection != null)
				return false;
		} else if (!activeConnection.equals(other.activeConnection))
			return false;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}
	
}
