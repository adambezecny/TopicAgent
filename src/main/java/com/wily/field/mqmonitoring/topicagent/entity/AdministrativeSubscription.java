package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 *
 * This class represents administrative subscription as retrieved by PCF Inquire Subscription
 *
 * It is important to understand that there is slight difference between administrative subscription and administrative topic 
 * (or rather {@link #AdministrativeTopic} class and AdministrativeSubscription class)
 * 
 * {@link #AdministrativeTopic} class (returned by PCF Inquire Topic) can really represent only  administratively defined topic object in MQ.
 * AdministrativeSubscription class (returned by PCF Inquire Subscription) can in principle represent either ADMIN, API or PROXY subscription! 
 * So it provides detailed configuration statistics of subscription OF ANY TYPE rather than actually represent ADMIN subscription only!
 * In this version of TopicAgent we are using AdministrativeSubscription object to capture configuration of ADMIN subscriptions only so that
 * these detailed metrics can be rendered. All ADMIN, PROXY and API subscriptions 
 * are also stored in map of Subscription objects (see member variable subscriptions of type Map<subscriptionId, Subscription> defined in Topic class) and used to calculate
 * summary metrics (total active subscribers, messages total, messages per interval/delta).
 *
 */
public class AdministrativeSubscription {

	/**
	 * For explanation of all topic attributes see IBM documentation:
	 * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088050_.htm
	 * 
	 * 
	 */
	
	private String subId;
	private byte[] subIdBytes;
	
	private String subName; 
	
	
	private String alterationDate;
	private String alterationTime; 
	private String creationDate;
	private String creationTime; 
	private String destination;
	
	private int destinationClass;
	private String destinationClassStr;
	
	private String destinationCorrelId; 
	private String destinationQueueManager; 
	
	private int expiry;
	private String expiryStr;
	
	private String publishedAccountingToken; 
	private String publishedApplicationIdentityData; 
	
	private int publishPriority;
	private String publishPriorityStr;
	
	private int publishSubscribeProperties;
	private String publishSubscribePropertiesStr;
	
	private int requestonly;
	private String requestonlyStr;
	
	private String selector;
	
	private int selectorType;
	private String selectorTypeStr;
	
	private int subscriptionLevel;
	
	private int subscriptionScope;
	private String subscriptionScopeStr;
	
	private int subscriptionType;
	private String subscriptionTypeStr;
	
	private String subscriptionUser; 
	private String topicObject;
	private String topicString; 
	private String userdata;
	
	private int variableUser; 
	private String variableUserStr;
	
	private int wildcardSchema;	
	private String wildcardSchemaStr;
	
	/**
	 * This constructor should be used for unit testing only 
	 * (i.e. so that we do not have to fill in everything when creating object)!
	 * @param subId
	 * @param subName
	 */
	protected AdministrativeSubscription(String subId, String subName){
		this.subId = subId;
		this.subName = subName;
	}
	
	public AdministrativeSubscription(String subId, byte[] subIdBytes, String subName,
			String alterationDate, String alterationTime, String creationDate,
			String creationTime, String destination, int destinationClass,
			String destinationClassStr, String destinationCorrelId,
			String destinationQueueManager, int expiry, String expiryStr,
			String publishedAccountingToken,
			String publishedApplicationIdentityData, int publishPriority,
			String publishPriorityStr, int publishSubscribeProperties,
			String publishSubscribePropertiesStr, int requestonly,
			String requestonlyStr, String selector, int selectorType,
			String selectorTypeStr, int subscriptionLevel,
			int subscriptionScope, String subscriptionScopeStr,
			int subscriptionType, String subscriptionTypeStr,
			String subscriptionUser, String topicObject, String topicString,
			String userdata, int variableUser, String variableUserStr,
			int wildcardSchema, String wildcardSchemaStr) {
		super();
		this.subId = subId;
		this.subIdBytes = subIdBytes;
		this.subName = subName;
		this.alterationDate = alterationDate;
		this.alterationTime = alterationTime;
		this.creationDate = creationDate;
		this.creationTime = creationTime;
		this.destination = destination.trim();
		this.destinationClass = destinationClass;
		this.destinationClassStr = destinationClassStr;
		this.destinationCorrelId = destinationCorrelId;
		this.destinationQueueManager = destinationQueueManager.trim();
		this.expiry = expiry;
		this.expiryStr = expiryStr;
		this.publishedAccountingToken = publishedAccountingToken;
		this.publishedApplicationIdentityData = publishedApplicationIdentityData.trim();
		this.publishPriority = publishPriority;
		this.publishPriorityStr = publishPriorityStr;
		this.publishSubscribeProperties = publishSubscribeProperties;
		this.publishSubscribePropertiesStr = publishSubscribePropertiesStr;
		this.requestonly = requestonly;
		this.requestonlyStr = requestonlyStr;
		this.selector = selector;
		this.selectorType = selectorType;
		this.selectorTypeStr = selectorTypeStr;
		this.subscriptionLevel = subscriptionLevel;
		this.subscriptionScope = subscriptionScope;
		this.subscriptionScopeStr = subscriptionScopeStr;
		this.subscriptionType = subscriptionType;
		this.subscriptionTypeStr = subscriptionTypeStr;
		this.subscriptionUser = subscriptionUser.trim();
		this.topicObject = topicObject.trim();
		this.topicString = topicString;
		this.userdata = userdata;
		this.variableUser = variableUser;
		this.variableUserStr = variableUserStr;
		this.wildcardSchema = wildcardSchema;
		this.wildcardSchemaStr = wildcardSchemaStr;
	}

	public String getSubId() {
		return subId;
	}

	public byte[] getSubIdBytes() {
		return subIdBytes;
	}

	
	public String getSubName() {
		return subName;
	}

	/**
	 * Returns subscription name in a way so that it can be incorporated into metric path.
	 * Basically replaces any potential '|' & ':' with '-'
	 * @return subscription name where any occurrences of '|' & ':' are replaced with '-'
	 */
	public String getSubNameToMetric() {
		return getSubName().replace('|', '-').replace(':', '-');
	}
	
	
	public String getAlterationDate() {
		return alterationDate;
	}

	public String getAlterationTime() {
		return alterationTime;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public String getDestination() {
		return destination;
	}

	public int getDestinationClass() {
		return destinationClass;
	}

	public String getDestinationClassStr() {
		return destinationClassStr;
	}

	public String getDestinationCorrelId() {
		return destinationCorrelId;
	}

	public String getDestinationQueueManager() {
		return destinationQueueManager;
	}

	public int getExpiry() {
		return expiry;
	}

	public String getExpiryStr() {
		return expiryStr;
	}
	
	
	public String getPublishedAccountingToken() {
		return publishedAccountingToken;
	}

	public String getPublishedApplicationIdentityData() {
		return publishedApplicationIdentityData;
	}

	public int getPublishPriority() {
		return publishPriority;
	}

	public String getPublishPriorityStr() {
		return publishPriorityStr;
	}

	public int getPublishSubscribeProperties() {
		return publishSubscribeProperties;
	}

	public String getPublishSubscribePropertiesStr() {
		return publishSubscribePropertiesStr;
	}

	public int getRequestonly() {
		return requestonly;
	}

	public String getRequestonlyStr() {
		return requestonlyStr;
	}

	public String getSelector() {
		return selector;
	}

	public int getSelectorType() {
		return selectorType;
	}

	public String getSelectorTypeStr() {
		return selectorTypeStr;
	}

	public int getSubscriptionLevel() {
		return subscriptionLevel;
	}

	public int getSubscriptionScope() {
		return subscriptionScope;
	}

	public String getSubscriptionScopeStr() {
		return subscriptionScopeStr;
	}

	public int getSubscriptionType() {
		return subscriptionType;
	}

	public String getSubscriptionTypeStr() {
		return subscriptionTypeStr;
	}

	public String getSubscriptionUser() {
		return subscriptionUser;
	}

	public String getTopicObject() {
		return topicObject;
	}

	public String getTopicString() {
		return topicString;
	}

	public String getUserdata() {
		return userdata;
	}

	public int getVariableUser() {
		return variableUser;
	}

	public String getVariableUserStr() {
		return variableUserStr;
	}

	public int getWildcardSchema() {
		return wildcardSchema;
	}

	public String getWildcardSchemaStr() {
		return wildcardSchemaStr;
	}

	@Override
	public String toString() {
		return "AdministrativeSubscription [subId=" + subId + ", subName="
				+ subName + ", alterationDate=" + alterationDate
				+ ", alterationTime=" + alterationTime + ", creationDate="
				+ creationDate + ", creationTime=" + creationTime
				+ ", destination=" + destination + ", destinationClass="
				+ destinationClass + ", destinationClassStr="
				+ destinationClassStr + ", destinationCorrelId="
				+ destinationCorrelId + ", destinationQueueManager="
				+ destinationQueueManager + ", expiry=" + expiry + ", expiryStr=" + expiryStr
				+ ", publishedAccountingToken=" + publishedAccountingToken
				+ ", publishedApplicationIdentityData="
				+ publishedApplicationIdentityData + ", publishPriority="
				+ publishPriority + ", publishPriorityStr="
				+ publishPriorityStr + ", publishSubscribeProperties="
				+ publishSubscribeProperties
				+ ", publishSubscribePropertiesStr="
				+ publishSubscribePropertiesStr + ", requestonly="
				+ requestonly + ", requestonlyStr=" + requestonlyStr
				+ ", selector=" + selector + ", selectorType=" + selectorType
				+ ", selectorTypeStr=" + selectorTypeStr
				+ ", subscriptionLevel=" + subscriptionLevel
				+ ", subscriptionScope=" + subscriptionScope
				+ ", subscriptionScopeStr=" + subscriptionScopeStr
				+ ", subscriptionType=" + subscriptionType
				+ ", subscriptionTypeStr=" + subscriptionTypeStr
				+ ", subscriptionUser=" + subscriptionUser + ", topicObject="
				+ topicObject + ", topicString=" + topicString + ", userdata="
				+ userdata + ", variableUser=" + variableUser
				+ ", variableUserStr=" + variableUserStr + ", wildcardSchema="
				+ wildcardSchema + ", wildcardSchemaStr=" + wildcardSchemaStr
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subId == null) ? 0 : subId.hashCode());
		result = prime * result + ((subName == null) ? 0 : subName.hashCode());
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
		AdministrativeSubscription other = (AdministrativeSubscription) obj;
		if (subId == null) {
			if (other.subId != null)
				return false;
		} else if (!subId.equals(other.subId))
			return false;
		if (subName == null) {
			if (other.subName != null)
				return false;
		} else if (!subName.equals(other.subName))
			return false;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}
	
	
}
