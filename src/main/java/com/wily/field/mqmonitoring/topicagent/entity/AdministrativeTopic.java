package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * This class represents administrative topic object (not topic node/topic/topic string!).
 *
 */
public class AdministrativeTopic {
	
	/**
	 * see http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088110_.htm 
	 * for meaning and possible values of each of the parameter below
	 */
	private String topicName;
	
	private int topicType;
	private String topicTypeStr;
	
	private String alterationDate; 
	private String alterationTime; 
	private String clusterName;
	private String custom;
	
	private int defPersistence;
	private String defPersistenceStr;
	
	private int defPriority;
	private String defPriorityStr;
	
	private int defPutResponse;
	private String defPutResponseStr;
	
	private String durableModelQName; 
	
	private int durableSubscriptions; 
	private String durableSubscriptionsStr;
	
	private int inhibitPublications;
	private String inhibitPublicationsStr;
	
	private int inhibitSubscriptions; 
	private String inhibitSubscriptionsStr;
	
	private String nonDurableModelQName; 
	
	private int nonPersistentMsgDelivery; 
	private String nonPersistentMsgDeliveryStr;
	
	private int persistentMsgDelivery;
	private String persistentMsgDeliveryStr;
	
	private int proxySubscriptions;
	private String proxySubscriptionsStr;
	
	private int publicationScope;
	private String publicationScopeStr;
	
	private String qmgrName;
	
	private int subscriptionScope;
	private String subscriptionScopeStr;
	
	private String topicDesc;
	private String topicString; 
	
	private int useDLQ;
	private String useDLQStr;
	
	private int wildcardOperation;
	private String wildcardOperationStr;
	
	/**
	 * For unit tests only!
	 * @param topicName
	 * @param topicType
	 * @param topicTypeStr
	 * @param clusterName
	 */
	public AdministrativeTopic(String topicName, int topicType,	String topicTypeStr, String clusterName){
		this.topicName = topicName;
		this.topicType = topicType;
		this.topicTypeStr = topicTypeStr;
		this.clusterName = clusterName;
	}
	
	public AdministrativeTopic(String topicName, int topicType,
			String topicTypeStr, String alterationDate, String alterationTime,
			String clusterName, String custom, int defPersistence,
			String defPersistenceStr, int defPriority, String defPriorityStr,
			int defPutResponse, String defPutResponseStr,
			String durableModelQName, int durableSubscriptions,
			String durableSubscriptionsStr, int inhibitPublications,
			String inhibitPublicationsStr, int inhibitSubscriptions,
			String inhibitSubscriptionsStr, String nonDurableModelQName,
			int nonPersistentMsgDelivery, String nonPersistentMsgDeliveryStr,
			int persistentMsgDelivery, String persistentMsgDeliveryStr,
			int proxySubscriptions, String proxySubscriptionsStr,
			int publicationScope, String publicationScopeStr, String qmgrName,
			int subscriptionScope, String subscriptionScopeStr,
			String topicDesc, String topicString, int useDLQ, String useDLQStr,
			int wildcardOperation, String wildcardOperationStr) {
		super();
		this.topicName = topicName;
		this.topicType = topicType;
		this.topicTypeStr = topicTypeStr;
		this.alterationDate = alterationDate;
		this.alterationTime = alterationTime;
		this.clusterName = clusterName;
		this.custom = custom;
		this.defPersistence = defPersistence;
		this.defPersistenceStr = defPersistenceStr;
		this.defPriority = defPriority;
		this.defPriorityStr = defPriorityStr;
		this.defPutResponse = defPutResponse;
		this.defPutResponseStr = defPutResponseStr;
		this.durableModelQName = durableModelQName;
		this.durableSubscriptions = durableSubscriptions;
		this.durableSubscriptionsStr = durableSubscriptionsStr;
		this.inhibitPublications = inhibitPublications;
		this.inhibitPublicationsStr = inhibitPublicationsStr;
		this.inhibitSubscriptions = inhibitSubscriptions;
		this.inhibitSubscriptionsStr = inhibitSubscriptionsStr;
		this.nonDurableModelQName = nonDurableModelQName;
		this.nonPersistentMsgDelivery = nonPersistentMsgDelivery;
		this.nonPersistentMsgDeliveryStr = nonPersistentMsgDeliveryStr;
		this.persistentMsgDelivery = persistentMsgDelivery;
		this.persistentMsgDeliveryStr = persistentMsgDeliveryStr;
		this.proxySubscriptions = proxySubscriptions;
		this.proxySubscriptionsStr = proxySubscriptionsStr;
		this.publicationScope = publicationScope;
		this.publicationScopeStr = publicationScopeStr;
		this.qmgrName = qmgrName;
		this.subscriptionScope = subscriptionScope;
		this.subscriptionScopeStr = subscriptionScopeStr;
		this.topicDesc = topicDesc;
		this.topicString = topicString;
		this.useDLQ = useDLQ;
		this.useDLQStr = useDLQStr;
		this.wildcardOperation = wildcardOperation;
		this.wildcardOperationStr = wildcardOperationStr;
	}


	public String getTopicName() {
		return topicName;
	}


	public int getTopicType() {
		return topicType;
	}


	public String getTopicTypeStr() {
		return topicTypeStr;
	}


	public String getAlterationDate() {
		return alterationDate;
	}


	public String getAlterationTime() {
		return alterationTime;
	}


	public String getClusterName() {
		return clusterName;
	}


	public String getCustom() {
		return custom;
	}


	public int getDefPersistence() {
		return defPersistence;
	}


	public String getDefPersistenceStr() {
		return defPersistenceStr;
	}


	public int getDefPriority() {
		return defPriority;
	}


	public String getDefPriorityStr() {
		return defPriorityStr;
	}


	public int getDefPutResponse() {
		return defPutResponse;
	}


	public String getDefPutResponseStr() {
		return defPutResponseStr;
	}


	public String getDurableModelQName() {
		return durableModelQName;
	}


	public int getDurableSubscriptions() {
		return durableSubscriptions;
	}


	public String getDurableSubscriptionsStr() {
		return durableSubscriptionsStr;
	}


	public int getInhibitPublications() {
		return inhibitPublications;
	}


	public String getInhibitPublicationsStr() {
		return inhibitPublicationsStr;
	}


	public int getInhibitSubscriptions() {
		return inhibitSubscriptions;
	}


	public String getInhibitSubscriptionsStr() {
		return inhibitSubscriptionsStr;
	}


	public String getNonDurableModelQName() {
		return nonDurableModelQName;
	}


	public int getNonPersistentMsgDelivery() {
		return nonPersistentMsgDelivery;
	}


	public String getNonPersistentMsgDeliveryStr() {
		return nonPersistentMsgDeliveryStr;
	}


	public int getPersistentMsgDelivery() {
		return persistentMsgDelivery;
	}


	public String getPersistentMsgDeliveryStr() {
		return persistentMsgDeliveryStr;
	}


	public int getProxySubscriptions() {
		return proxySubscriptions;
	}


	public String getProxySubscriptionsStr() {
		return proxySubscriptionsStr;
	}


	public int getPublicationScope() {
		return publicationScope;
	}


	public String getPublicationScopeStr() {
		return publicationScopeStr;
	}


	public String getQmgrName() {
		return qmgrName;
	}


	public int getSubscriptionScope() {
		return subscriptionScope;
	}


	public String getSubscriptionScopeStr() {
		return subscriptionScopeStr;
	}


	public String getTopicDesc() {
		return topicDesc;
	}


	public String getTopicString() {
		return topicString;
	}


	public int getUseDLQ() {
		return useDLQ;
	}


	public String getUseDLQStr() {
		return useDLQStr;
	}


	public int getWildcardOperation() {
		return wildcardOperation;
	}


	public String getWildcardOperationStr() {
		return wildcardOperationStr;
	}


	@Override
	public String toString() {
		return "AdministrativeTopic [topicName=" + topicName + ", topicType="
				+ topicType + ", topicTypeStr=" + topicTypeStr
				+ ", alterationDate=" + alterationDate + ", alterationTime="
				+ alterationTime + ", clusterName=" + clusterName + ", custom="
				+ custom + ", defPersistence=" + defPersistence
				+ ", defPersistenceStr=" + defPersistenceStr + ", defPriority="
				+ defPriority + ", defPriorityStr=" + defPriorityStr
				+ ", defPutResponse=" + defPutResponse + ", defPutResponseStr="
				+ defPutResponseStr + ", durableModelQName="
				+ durableModelQName + ", durableSubscriptions="
				+ durableSubscriptions + ", durableSubscriptionsStr="
				+ durableSubscriptionsStr + ", inhibitPublications="
				+ inhibitPublications + ", inhibitPublicationsStr="
				+ inhibitPublicationsStr + ", inhibitSubscriptions="
				+ inhibitSubscriptions + ", inhibitSubscriptionsStr="
				+ inhibitSubscriptionsStr + ", nonDurableModelQName="
				+ nonDurableModelQName + ", nonPersistentMsgDelivery="
				+ nonPersistentMsgDelivery + ", nonPersistentMsgDeliveryStr="
				+ nonPersistentMsgDeliveryStr + ", persistentMsgDelivery="
				+ persistentMsgDelivery + ", persistentMsgDeliveryStr="
				+ persistentMsgDeliveryStr + ", proxySubscriptions="
				+ proxySubscriptions + ", proxySubscriptionsStr="
				+ proxySubscriptionsStr + ", publicationScope="
				+ publicationScope + ", publicationScopeStr="
				+ publicationScopeStr + ", qmgrName=" + qmgrName
				+ ", subscriptionScope=" + subscriptionScope
				+ ", subscriptionScopeStr=" + subscriptionScopeStr
				+ ", topicDesc=" + topicDesc + ", topicString=" + topicString
				+ ", useDLQ=" + useDLQ + ", useDLQStr=" + useDLQStr
				+ ", wildcardOperation=" + wildcardOperation
				+ ", wildcardOperationStr=" + wildcardOperationStr + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
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
		AdministrativeTopic other = (AdministrativeTopic) obj;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}
	
}
