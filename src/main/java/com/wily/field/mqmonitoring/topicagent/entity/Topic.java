package com.wily.field.mqmonitoring.topicagent.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * This class represents MQ Topic. It represents topic node/topic string/topic
 * in topic space/tree of queue manager. If administrative topic object is associated
 * in topic space with topic string represented by this instance it is attached in attribute
 * 'administrativeTopic' and also its name is in 'adminTopicName'. Yet it is important to understand that
 * topic (Topic class) IS NOT same as administrative topic (AdministartiveTopic) class.
 * 
 * See for example following article explaining the difference: 
 * 
 * https://www.ibm.com/developerworks/community/blogs/messaging/entry/mq_topics_but_which_type?lang=en  
 *
 */
public class Topic {

	
	/**
	 * For explanation of all topic attributes see IBM documentation:
	 * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088150_.htm
	 */
    private TopicString topicString;
    private String cluster;
    
    private int defPriority;
    
    private int defaultPutResponse;
    private String defaultPutResponseStr;
    
    private int defPersistence;
    private String defPersistenceStr;
    
    private int durableSubscriptions;
    private String durableSubscriptionsStr;
    
    private int inhibitPublications;
    private String inhibitPublicationsStr;
    
    private int inhibitSubscriptions; 
    private String inhibitSubscriptionsStr;
    
    private String adminTopicName;
    private String durableModelQName; 
    private String nonDurableModelQName; 
    
    private int persistentMessageDelivery;
    private String persistentMessageDeliveryStr;
    
    private int nonPersistentMessageDelivery;
    private String nonPersistentMessageDeliveryStr;
    
    private int retainedPublication;
    private String retainedPublicationStr;
    
    private int publishCount;
    
    private int subscriptionScope;
    private String subscriptionScopeStr;
    
    private int subscriptionCount;
    
    private int publicationScope;
    private String publicationScopeStr;
    
    private int useDLQ;    
    private String useDLQStr;

    
    /** 
     * Stores all publications of this Topic
     * Data for each publication is retrieved by PCF 'Inquire Topic Status Pub'
     * HashMap key is activeConnection(connectionId), there is no uniqueueID
     * as we have for subscriptions so conenctionId is all we can use here to
     * match and compare publications across cycles, see {@link #getPublicationDelta()}
     */
    private Map<String, Publication> publications;
    
    
    /**
     * Stores all subscriptions of this Topic, i.e. ADMIN, API & PROXY
     * Data for each subscription is retrieved by PCF 'Inquire Topic Status Sub'
     * HashMap key is subscriptionId. 
     * Each subscription has unique subscriptionId but administrative subscriptions
     * defined on one queue manager have same activeConnection/connectionId (0000..)!
     * This is the reason why we need to use subscriptionId to distinguish and match
     * subscriptions across cycles!
     * 
     */
    private Map<String, Subscription> subscriptions;

    
    /**
     * Stores additional data (retrievable by PCF 'Inquire Subscription')
     * of administrative subscriptions of this topic! In principle 
     * AdministrativeSubscription object can be used to display configuration
     * data of all subscription types (ADMIN, API, PROXY) but we are displaying
     * on per-topic level detailed data only for ADMIN subscriptions!
    */
    private ArrayList<AdministrativeSubscription> administrativeSubscriptions;
    
    
    private AdministrativeTopic administrativeTopic;//key is TopicName 
    
    private Topic(){
    	
		administrativeSubscriptions = new ArrayList<AdministrativeSubscription>();
		publications = new HashMap<String, Publication>();
		subscriptions = new HashMap<String, Subscription>();
    	
    }

    /**
     * This constructor should be used for unit testing only 
     * (i.e. so that we do not have to fill in everything when creating object)!
     * appropriate fields should be then set using java reflection. It is public so that it can be called
     * from different packages. Other option would be to make it private and call it via reflection
     * See: 
     * {@link com.wily.field.mqmonitoring.topicagent.entity.TopicsTest#testTopicsClass()}
     * {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterTest#testLocalVersusClusterTopicFolder()}
     * 
     * @param topicString
     * @param adminTopicName
     */
    public Topic(TopicString topicString, String adminTopicName){
    	this();
    	this.topicString = topicString;
    	this.adminTopicName = adminTopicName;
    }
    
	public Topic(TopicString topicString, String cluster, int defPriority,
			int defaultPutResponse,
			String defaultPutResponseStr, int defPersistence,
			String defPersistenceStr, int durableSubscriptions,
			String durableSubscriptionsStr, int inhibitPublications,
			String inhibitPublicationsStr, int inhibitSubscriptions,
			String inhibitSubscriptionsStr, String adminTopicName,
			String durableModelQName, String nonDurableModelQName,
			int persistentMessageDelivery, String persistentMessageDeliveryStr,
			int nonPersistentMessageDelivery,
			String nonPersistentMessageDeliveryStr, int retainedPublication,
			String retainedPublicationStr, int publishCount,
			int subscriptionScope,
			String subscriptionScopeStr, int subscriptionCount,
			int publicationScope,
			String publicationScopeStr, int useDLQ, String useDLQStr) {
		
		this();
		
		this.topicString = topicString;
		this.cluster = cluster;
		this.defPriority = defPriority;
		this.defaultPutResponse = defaultPutResponse;
		this.defaultPutResponseStr = defaultPutResponseStr;
		this.defPersistence = defPersistence;
		this.defPersistenceStr = defPersistenceStr;
		this.durableSubscriptions = durableSubscriptions;
		this.durableSubscriptionsStr = durableSubscriptionsStr;
		this.inhibitPublications = inhibitPublications;
		this.inhibitPublicationsStr = inhibitPublicationsStr;
		this.inhibitSubscriptions = inhibitSubscriptions;
		this.inhibitSubscriptionsStr = inhibitSubscriptionsStr;
		this.adminTopicName = adminTopicName;
		this.durableModelQName = durableModelQName;
		this.nonDurableModelQName = nonDurableModelQName;
		this.persistentMessageDelivery = persistentMessageDelivery;
		this.persistentMessageDeliveryStr = persistentMessageDeliveryStr;
		this.nonPersistentMessageDelivery = nonPersistentMessageDelivery;
		this.nonPersistentMessageDeliveryStr = nonPersistentMessageDeliveryStr;
		this.retainedPublication = retainedPublication;
		this.retainedPublicationStr = retainedPublicationStr;
		this.publishCount = publishCount;
		this.subscriptionScope = subscriptionScope;
		this.subscriptionScopeStr = subscriptionScopeStr;
		this.subscriptionCount = subscriptionCount;
		this.publicationScope = publicationScope;
		this.publicationScopeStr = publicationScopeStr;
		this.useDLQ = useDLQ;
		this.useDLQStr = useDLQStr;
		
	}

	
	public TopicString getTopicString() {
		return topicString;
	}

	public String getCluster() {
		return cluster;
	}

	public int getDefPriority() {
		return defPriority;
	}

	public int getDefaultPutResponse() {
		return defaultPutResponse;
	}

	public String getDefaultPutResponseStr() {
		return defaultPutResponseStr;
	}

	public int getDefPersistence() {
		return defPersistence;
	}

	public String getDefPersistenceStr() {
		return defPersistenceStr;
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

	public String getAdminTopicName() {
		return adminTopicName;
	}

	public String getDurableModelQName() {
		return durableModelQName;
	}

	public String getNonDurableModelQName() {
		return nonDurableModelQName;
	}

	public int getPersistentMessageDelivery() {
		return persistentMessageDelivery;
	}

	public String getPersistentMessageDeliveryStr() {
		return persistentMessageDeliveryStr;
	}

	public int getNonPersistentMessageDelivery() {
		return nonPersistentMessageDelivery;
	}

	public String getNonPersistentMessageDeliveryStr() {
		return nonPersistentMessageDeliveryStr;
	}

	public int getRetainedPublication() {
		return retainedPublication;
	}

	public String getRetainedPublicationStr() {
		return retainedPublicationStr;
	}

	public int getPublishCount() {
		return publishCount;
	}

	public int getSubscriptionScope() {
		return subscriptionScope;
	}

	public String getSubscriptionScopeStr() {
		return subscriptionScopeStr;
	}

	public int getSubscriptionCount() {
		return subscriptionCount;
	}

	public int getPublicationScope() {
		return publicationScope;
	}

	public String getPublicationScopeStr() {
		return publicationScopeStr;
	}

	public int getUseDLQ() {
		return useDLQ;
	}

	public String getUseDLQStr() {
		return useDLQStr;
	}

	public Map<String, Subscription> getSubscriptions() {
		return this.subscriptions;
	}
	
	
	public AdministrativeTopic getAdministrativeTopic() {
		return administrativeTopic;
	}


	public void setAdministrativeTopic(AdministrativeTopic administrativeTopic) {
		this.administrativeTopic = administrativeTopic;
	}


	public void addSubscription(Subscription subscription){
		this.subscriptions.put(subscription.getSubscriptionId(), subscription);
	}

	
	public List<AdministrativeSubscription> getAdministrativeSubscriptions() {
		return this.administrativeSubscriptions;
	}
	
	public void addAdministrativeSubscription(AdministrativeSubscription subscription){
		this.administrativeSubscriptions.add(subscription);
	}

	
	public Map<String, Publication> getPublications() {
		return this.publications;
	}
	
	/**
	 * Ads the publication into internal HaspMap of topic's publications.
	 * If given key (connectionId/activeConnection of publication) already
	 * exists it rather updates existing object, i.e. it adds number of messages
	 * published by this connectionId. This must be implemented like this 
	 * due to the behavior of MQ when it reports separate publication objects
	 * with same activeConnection ID in regular intervals during publication session
	 * rather than reporting only one object with incremented numOfPublishesExisting attribute.
	 * 
	 * @param publication
	 */
	public void addOrUpdatePublication(Publication publication){
		
		if(this.publications.containsKey(publication.getActiveConnection())){
			Publication pubExisting = this.publications.get(publication.getActiveConnection());
			int numOfPublishesExisting = pubExisting.getNumberOfPublishes();
			int numOfPublishesNew = publication.getNumberOfPublishes();
			publication.setNumberOfPublishes(numOfPublishesExisting + numOfPublishesNew);
		}
		
		this.publications.put(publication.getActiveConnection(), publication);
		
	}

	/**
	 * Returns number of publications/publishers associated with this Topic
	 * @return
	 */
	public int getPublisherCount(){
		return this.publications.size();
	}
	

	/**
	 * Returns number of subscriptions/subscribers associated with this Topic
	 * @param subscriptionType - Type of subscription to include into calculation, see {@link #com.wily.field.mqmonitoring.topicagent.entity.SubscriptionType}}
	 * @return
	 */
	public int getSubscriberCount(SubscriptionType subscriptionType){
		
		int count = 0;
		
		if(subscriptionType == SubscriptionType.ALL)
			return this.subscriptions.size();
		
		for(Map.Entry<String, Subscription> entrySub : this.subscriptions.entrySet()){
			Subscription iteratedSubscription = entrySub.getValue();
			if(subscriptionTypeDoesMatch(subscriptionType, iteratedSubscription)) count++; 
		}
		
		return count;
		
	}
	
	/**
	 * Returns sum of all attributes NumberOfPublishes for each publication assigned to this Topic
	 * NumberOfPublishes of publication represents: Number of publishes made by this publisher
	 * @return Summary of numbers of publishes made by all publishers
	 */
	public int getPublishedMessagesCount(){
		
		int count = 0;
		
		for(Map.Entry<String, Publication> entryPub : this.publications.entrySet()){
			
			Publication iteratedPublication = entryPub.getValue();
			count += iteratedPublication.getNumberOfPublishes();
		}			
		
		return count;
		
	}
	
	/**
	 * Returns sum of all attributes NumberOfMessages for each subscription assigned with this Topic
	 * NumberOfMessages of subscription represents: Number of messages put to the destination specified by this subscription
	 * @param subscriptionType - Type of subscription to include into calculation, see {@link #com.wily.field.mqmonitoring.topicagent.entity.SubscriptionType}}
	 * @return Summary of numbers of messages put to the destinations for all subscriptions
	 */
	public int getSubscribedMessagesCount(SubscriptionType subscriptionType){
		
		int count = 0;
		
		for(Map.Entry<String, Subscription> entrySub : this.subscriptions.entrySet()){
			Subscription iteratedSubscription = entrySub.getValue();
			if(subscriptionTypeDoesNotMatch(subscriptionType, iteratedSubscription)) continue;
			count += iteratedSubscription.getNumberOfMessages();
		}			
		
		return count;
	}
	
	/**
	 * Convenience method providing reverse result as {@link #subscriptionTypeDoesNotMatch}
	 * @param subscriptionType
	 * @param subscription
	 * @return
	 */
	private static boolean subscriptionTypeDoesMatch(SubscriptionType subscriptionType, Subscription subscription){
		return !subscriptionTypeDoesNotMatch(subscriptionType, subscription);
	}
	
	/**
	 * checks if subscription type matches the type given by subscriptionType parameter
	 * @param subscriptionType - type we are checking
	 * @param subscription - subscription to check
	 * @return - true if subscription type does not match type indicated by subscriptionType, false otherwise (or if subscriptionType = ALL)
	 */
	private static boolean subscriptionTypeDoesNotMatch(SubscriptionType subscriptionType, Subscription subscription){
		
		if(subscriptionType == SubscriptionType.ALL) return false;//SubscriptionType.ALL matches everything!
		
		if( (subscriptionType == SubscriptionType.ADMIN && !subscription.getSubscriptionTypeStr().equals(Subscription.SUBSCRIPTION_TYPE_STR_ADMIN)) ||
			(subscriptionType == SubscriptionType.API   && !subscription.getSubscriptionTypeStr().equals(Subscription.SUBSCRIPTION_TYPE_STR_API))   || 
			(subscriptionType == SubscriptionType.PROXY && !subscription.getSubscriptionTypeStr().equals(Subscription.SUBSCRIPTION_TYPE_STR_PROXY))
		  ) 
			return true; 
		else 
			return false;
		
		
	}
	
	/**
	 * 
	 * This method compares subscriptions for particular topic string in current and previous cycle and returns
	 * delta metric showing how much more/additional messages were processed/subscribed by subscriptions between
	 * these two cycles. Method is comparing/matching publishers by subscriptionId!
	 * 
	 * For exhaustive/elaborate list of situations that might happen 
	 * see {@link com.wily.field.mqmonitoring.topicagent.entity.TopicTest#getSubscriptionMessageCountDelta()}
	 * 
	 * @param topicCurrentCycle - Topic in current cycle
	 * @param topicPreviousCycle - Same topic in previous cycle
	 * @param subscriptionType - Type of subscription to include into calculation, see {@link #com.wily.field.mqmonitoring.topicagent.entity.SubscriptionType}}
	 * @return
	 */
	public static int getSubscriptionMessageCountDelta(Topic topicCurrentCycle, Topic topicPreviousCycle, SubscriptionType subscriptionType){
		
		int delta = 0;
		
		Map<String, Subscription> subscriptionsCurrentCycle  = topicCurrentCycle.getSubscriptions();
		Map<String, Subscription> subscriptionsPreviousCycle = topicPreviousCycle.getSubscriptions();
		
		//first get unique set of subscriptionIds contained in both current and previous cycle ...
		HashSet<String> uniqueueSubscriptionIds = new HashSet<String>();
		
		for(Map.Entry<String, Subscription> entrySub : subscriptionsCurrentCycle.entrySet()){
			Subscription iteratedSubscription = entrySub.getValue();
			if(subscriptionTypeDoesNotMatch(subscriptionType, iteratedSubscription)) continue;
			uniqueueSubscriptionIds.add(iteratedSubscription.getSubscriptionId());
		}
		
		for(Map.Entry<String, Subscription> entrySub : subscriptionsPreviousCycle.entrySet()){
			Subscription iteratedSubscription = entrySub.getValue();
			if(subscriptionTypeDoesNotMatch(subscriptionType, iteratedSubscription)) continue;
			uniqueueSubscriptionIds.add(iteratedSubscription.getSubscriptionId());
		}
		
		//... then iterate over all subscriptionIds and calculate partial deltas and summary delta
		for(String subscriptionId : uniqueueSubscriptionIds){
			
			if(subscriptionsCurrentCycle.containsKey(subscriptionId) && subscriptionsPreviousCycle.containsKey(subscriptionId)){
				//if particular subscriptionId is in both current and previous cycle do delta
				delta += ( subscriptionsCurrentCycle.get(subscriptionId).getNumberOfMessages() - subscriptionsPreviousCycle.get(subscriptionId).getNumberOfMessages() );
			}else if(subscriptionsCurrentCycle.containsKey(subscriptionId) && !subscriptionsPreviousCycle.containsKey(subscriptionId)){
				//if particular subscriptionId is in currentCycle only add to delta number of messages processed by subscription in current cycle
				//(i.e. there is nothing to deduct from previous cycle)
				delta += subscriptionsCurrentCycle.get(subscriptionId).getNumberOfMessages();
			}else{
				//if particular subscriptionId is in previous cycle only disregard this subscription and do not
				//include it into delta since no new messages have been subscribed by this subscriptionId in current cycle
			}
				
		}
		
		return delta;
		
		
	}

	/**
	 * 
	 * This method compares publishers for particular topic string in current and previous cycle and returns
	 * delta metric showing how much more/additional messages were published by publishers between
	 * these two cycles. Method is comparing/matching publishers by connectionId!
	 * 
	 * For exhaustive/elaborate list of situations that might happen 
	 * see {@link com.wily.field.mqmonitoring.topicagent.entity.TopicTest#getPublicationMessageCountDeltaTest()}
	 * 
	 * @param topicCurrentCycle
	 * @param topicPreviousCycle
	 * @return
	 */
	public static int getPublicationMessageCountDelta(Topic topicCurrentCycle, Topic topicPreviousCycle){
		
		int delta = 0;
		
		Map<String, Publication> publicationsCurrentCycle  = topicCurrentCycle.getPublications();
		Map<String, Publication> publicationsPreviousCycle = topicPreviousCycle.getPublications();
		
		//first get unique set of conenctionIds contained in both current and previous cycle ...
		HashSet<String> uniqueueConnectionIds = new HashSet<String>();
		
		for(Map.Entry<String, Publication> entryPub : publicationsCurrentCycle.entrySet()){
			Publication iteratedPublication = entryPub.getValue();
			uniqueueConnectionIds.add(iteratedPublication.getActiveConnection());
		}
		
		for(Map.Entry<String, Publication> entryPub : publicationsPreviousCycle.entrySet()){
			Publication iteratedPublication = entryPub.getValue();
			uniqueueConnectionIds.add(iteratedPublication.getActiveConnection());
		}
		
		//... then iterate over all connectionIds and calculate partial deltas and summary delta
		for(String connectionId : uniqueueConnectionIds){
			
			if(publicationsCurrentCycle.containsKey(connectionId) && publicationsPreviousCycle.containsKey(connectionId)){
				//if particular connectionId is in both current and previous cycle do delta
				delta += ( publicationsCurrentCycle.get(connectionId).getNumberOfPublishes() - publicationsPreviousCycle.get(connectionId).getNumberOfPublishes() );
			}else if(publicationsCurrentCycle.containsKey(connectionId) && !publicationsPreviousCycle.containsKey(connectionId)){
				//if particular connectionId is in currentCycle only add to delta number of published messages from current cycle
				//(i.e. there is nothing to deduct from previous cycle)
				delta += publicationsCurrentCycle.get(connectionId).getNumberOfPublishes();
			}else{
				//if particular connectionId is in previous cycle only disregard this publisher and do not
				//include it into delta since no new messages have been published on this connection ID!
			}
				
		}
		
		return delta;
	}
	
	@Override
	public String toString() {
		return "Topic [topicString=" + topicString + ", cluster=" + cluster
				+ ", defPriority=" + defPriority 
				+ ", defaultPutResponse=" + defaultPutResponse
				+ ", defaultPutResponseStr=" + defaultPutResponseStr
				+ ", defPersistence=" + defPersistence + ", defPersistenceStr="
				+ defPersistenceStr + ", durableSubscriptions="
				+ durableSubscriptions + ", durableSubscriptionsStr="
				+ durableSubscriptionsStr + ", inhibitPublications="
				+ inhibitPublications + ", inhibitPublicationsStr="
				+ inhibitPublicationsStr + ", inhibitSubscriptions="
				+ inhibitSubscriptions + ", inhibitSubscriptionsStr="
				+ inhibitSubscriptionsStr + ", adminTopicName="
				+ adminTopicName + ", durableModelQName=" + durableModelQName
				+ ", nonDurableModelQName=" + nonDurableModelQName
				+ ", persistentMessageDelivery=" + persistentMessageDelivery
				+ ", persistentMessageDeliveryStr="
				+ persistentMessageDeliveryStr
				+ ", nonPersistentMessageDelivery="
				+ nonPersistentMessageDelivery
				+ ", nonPersistentMessageDeliveryStr="
				+ nonPersistentMessageDeliveryStr + ", retainedPublication="
				+ retainedPublication + ", retainedPublicationStr="
				+ retainedPublicationStr + ", publishCount=" + publishCount
				+ ", subscriptionScope=" + subscriptionScope
				+ ", subscriptionScopeStr=" + subscriptionScopeStr
				+ ", subscriptionCount=" + subscriptionCount
				+ ", publicationScope=" + publicationScope
				+ ", publicationScopeStr=" + publicationScopeStr + ", useDLQ="
				+ useDLQ + ", useDLQStr=" + useDLQStr + ", subscriptions="
				+ subscriptions + ", administrativeSubscriptions="
				+ administrativeSubscriptions + ", publications="
				+ publications + ", administrativeTopic=" + administrativeTopic
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Topic other = (Topic) obj;
		if (topicString == null) {
			if (other.topicString != null)
				return false;
		} else if (!topicString.equals(other.topicString))
			return false;
		return true;
	}
    
	
}
