package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * This class represents MQ Topic Subscription as retrieved by PCF Inquire Topic Status (SUB)
 * See also {@link #AdministrativeSubscription} in order to fully understand the difference
 * and relation ship between Subscription and AdministrativeSubscription 
 * (and also between classes {@link #AdministrativeSubscription} and {@link #AdministrativeTopic})
 */
public class Subscription {

	/**
	 * For explanation of all topic attributes see IBM documentation:
	 * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088150_.htm
	 */
	private TopicString topicString;
	private String subscriptionId;
	private String subscriptionUserId;
	private int durable;
	
	private int subscriptionType;
	private String subscriptionTypeStr;
	
	private String resumeDate;
	private String resumeTime; 
	private String lastMessageDate;
	private String lastMessageTime; 
	private int numberOfMessages;
	private String activeConnection;

   public static final String SUBSCRIPTION_TYPE_STR_ADMIN = "MQSUBTYPE_ADMIN";
   public static final String SUBSCRIPTION_TYPE_STR_API = "MQSUBTYPE_API";
   public static final String SUBSCRIPTION_TYPE_STR_PROXY = "MQSUBTYPE_PROXY";

	/**
	 * For unit tests only!
	 * @param topicString
	 * @param subscriptionId
	 * @param numberOfMessages
	 * @param activeConnection
	 */
	public Subscription(TopicString topicString, String subscriptionId,int numberOfMessages, String activeConnection, String subscriptionTypeStr){
		this.topicString = topicString;
		this.subscriptionId = subscriptionId;
		this.numberOfMessages = numberOfMessages;
		this.activeConnection = activeConnection;
		this.subscriptionTypeStr = subscriptionTypeStr;
	}
	
	public Subscription(TopicString topicString, String subscriptionId, String subscriptionUserId,
			int durable, int subscriptionType, String subscriptionTypeStr, String resumeDate,
			String resumeTime, String lastMessageDate, String lastMessageTime,
			int numberOfMessages, String activeConnection) {
		super();
		this.topicString = topicString;
		this.subscriptionId = subscriptionId;
		this.subscriptionUserId = subscriptionUserId;
		this.durable = durable;
		this.subscriptionType = subscriptionType;
		this.subscriptionTypeStr = subscriptionTypeStr;
		this.resumeDate = resumeDate;
		this.resumeTime = resumeTime;
		this.lastMessageDate = lastMessageDate;
		this.lastMessageTime = lastMessageTime;
		this.numberOfMessages = numberOfMessages;
		this.activeConnection = activeConnection;
	}


	public String getSubscriptionTypeStr() {
		return subscriptionTypeStr;
	}


	public String getSubscriptionId() {
		return subscriptionId;
	}


	public String getSubscriptionUserId() {
		return subscriptionUserId;
	}


	public int getDurable() {
		return durable;
	}


	public int getSubscriptionType() {
		return subscriptionType;
	}


	public String getResumeDate() {
		return resumeDate;
	}


	public String getResumeTime() {
		return resumeTime;
	}


	public String getLastMessageDate() {
		return lastMessageDate;
	}


	public String getLastMessageTime() {
		return lastMessageTime;
	}


	public int getNumberOfMessages() {
		return numberOfMessages;
	}


	public String getActiveConnection() {
		return activeConnection;
	}

	public TopicString getTopicString() {
		return topicString;
	}


	@Override
	public String toString() {
		return "Subscription [topicString=" + topicString + ", subscriptionId="
				+ subscriptionId + ", subscriptionUserId=" + subscriptionUserId
				+ ", durable=" + durable + ", subscriptionType="
				+ subscriptionType + ", subscriptionTypeStr="
				+ subscriptionTypeStr + ", resumeDate=" + resumeDate
				+ ", resumeTime=" + resumeTime + ", lastMessageDate="
				+ lastMessageDate + ", lastMessageTime=" + lastMessageTime
				+ ", numberOfMessages=" + numberOfMessages
				+ ", activeConnection=" + activeConnection + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
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
		Subscription other = (Subscription) obj;
		if (subscriptionId == null) {
			if (other.subscriptionId != null)
				return false;
		} else if (!subscriptionId.equals(other.subscriptionId))
			return false;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}

}
