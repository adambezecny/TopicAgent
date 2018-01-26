package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.util.Date;
import java.util.Map;

import com.wily.field.mqmonitoring.topicagent.RuntimeSettings;
import com.wily.field.mqmonitoring.topicagent.entity.*;
import com.wily.field.steadydatareporter.SteadyDataReporterManager;

/**
 * This class contains business logic for writing TopicAgent metrics. It also defines abstract method to be overridden by its descendants. 
 * Currently 3 metric writers are implemented:
 * 
 * {@link #MetricWriterDummy} - metrics are written to standard output only, suitable for unit testing
 * {@link #MetricWriterISC} - metrics are written utilizing internal APIs of EPAgent, this one is used in real deployment
 * {@link #MetricWriterREST} - metrics are written utilizing EPAgent RESTFull/JSON interface. Suitable for debugging the code from Eclipse while connecting to external EPAgent
 * 
 *  
 * @author Adam Bezecny - CA Services
 *
 */
public abstract class MetricWriter {

    private static final String TOPICS_SPACE = "|Topic Space|";
    private static final String MQ_TOPIC_MONITOR = "MQ Topic Monitor|";
    
    private static final String TOPIC_CONFIG = "|_Configuration:";
    private static final String TOPIC_STATUS = "|_Status:";
    
    private static final String TOPIC_CONFIG_NO_UNDERSCORE = "|Configuration:";
    
    @SuppressWarnings("unused")
	private static final String TOPIC_STATUS_NO_UNDERSCORE = "|Status:";
    
    
    private static final String ADMIN_TOPIC = "|_AdminTopic";
    
    private static final String TOPIC_LOCAL = "|Local Topic";
    private static final String TOPIC_CLUSTER = "|Cluster Topic";
    
    private static final String TOPIC_PUBS = "|_Publications:";
    private static final String NUMBER_OF_ACTIVE_PUBLISHERS = "Number Of Active Publishers";
    private static final String PUBLICATION_MESSAGES_COUNT_TOTAL = "Published Messages Total";
    private static final String PUBLICATION_MESSAGES_COUNT_DELTA = "Published Messages Per Interval";
    
    private static final String TOPIC_SUBS = "|_Subscriptions:";
    private static final String TOPIC_ADMIN_SUBS = "|_Subscriptions|Administrative Subscriptions|";
    private static final String TOPIC_ADMIN_SUBS2 = "|_Subscriptions|Administrative Subscriptions:";
    private static final String TOPIC_API_SUBS = "|_Subscriptions|API Subscriptions:";
    private static final String TOPIC_PROXY_SUBS = "|_Subscriptions|Proxy Subscriptions:";
    private static final String SUBSCRIPTION_MESSAGES_COUNT_TOTAL = "Number Of Messages Total";
    private static final String SUBSCRIPTION_MESSAGES_COUNT_DELTA = "Number Of Messages Per Interval";
    private static final String NUMBER_OF_ACTIVE_SUBSCRIBERS = "Number Of Active Subscribers";
    
    public static final int QUEUEMANAGER_CONNECTION_OK = 1;
    public static final int QUEUEMANAGER_CONNECTION_KO = 2;
    
	private int pollingInterval;
	protected SteadyDataReporterManager steadyDataManager = null;
	
    private final String queueManager;
    
    protected MetricWriter(String queueManager, int pollingInterval){
    	this.queueManager=normalizeQueueManagerName(queueManager);
    	this.pollingInterval = pollingInterval;
    	
    	if(this.pollingInterval > 15){
    		
    		/**
    		 * 
    		 * Using SteadyDataReporterManager is highly experimental feature which might result
    		 * in additional performance overhead and also imprecise results from time to time
    		 * for metrics where it is used (currently number of active publishers and subscribers)!
    		 * The goal is not to use it when not absolutely necessary! For some clients zero-spikes/areas in LongAverage
    		 * metrics when using polling interval longer than 15 seconds might be still more acceptable
    		 * than additional risk resulting from using this component.  
    		 * 
    		 * The choice is with you... ;)
    		 * 
    		 * Ideal case is when 15 seconds polling interval is sufficient and no zero-spikes/areas for LongAverage
    		 * metrics can possibly/in principle happen. In this case implementation is not using SteadyDataReporterManager but
    		 * rather reporting LongAverage metrics directly (steadyDataManager = null in that case).
    		 * 
    		 */
    		
    		//e.g. if polling interval is 60 seconds we need to do 3 forwarding cycles
    		//i.e. 60/15-1=3
    		int forwardingCycles = (this.pollingInterval / 15) - 1; 
    		this.steadyDataManager = new SteadyDataReporterManager(this, forwardingCycles);
    		
    	}
    }
	
    /**
     * Forces the metric to exist, otherwise it would not be shown until very first disconnect error
     */
    public final void forceSupportabilityMetrics(){
    	forceExistPerIntervalCounter("MQ Topic Monitor|" + this.queueManager + "|Supportability:Disconnect Errors");
    }
    
    /**
     * Used to report queue manager connection status. 
     * This metric is written from {@link com.wily.field.mqmonitoring.topicagent.TopicAgent#monitoringLoop()} 
     * @param status 1-Connection with queue manager OK, 
     * 				 2 - Queue manager connection error (one of the following codes: MQRC_CONNECTION_BROKEN or MQRC_CONNECTION_ERROR or MQRC_CONNECTION_NOT_AUTHORIZED)
     */
    public final void writeQueuemanagerConnectionStatusMetric(int status){
    	writeLongAverage("MQ Topic Monitor|" + this.queueManager + ":ConnectionStatus", status);
    }
    
    /**
     * Convenience method used to write all supportability metrics
     * @param metrics Supportability metric type to write
     * @param arguments Dynamic list of arguments, differs for particular supportability metric types
     */
    public final void writeSupportabilityMetric(SupportabilityMetrics metrics, Object... arguments){
    	
    	
    	switch(metrics){
    		case PCF_SEND_MESSAGE_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:PCF Message Send Time (ms)", ((Long)arguments[1]).longValue());
    			break;
    			
    		case PCF_SEND_MESSAGE_ERROR:
                writePerIntervalCounter("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:PCF Request Errors");
                
                //this might potentially break the pipe between plug-in and EPAgent if it is not formatted correctly (I saw it during MQ disconnect error)
                //resulting in whole EPAgent being shutdown! I'll better comment it, EPA log entry will be sufficient.
                //writeErrorDetectorEntry("MQ Topic Monitor", "Error executing PCF Message: " + (String)arguments[1]);
    			break;

    		case PCF_DISCONNECT_ERROR:
                writePerIntervalCounter("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability:Disconnect Errors");

                //this might potentially break the pipe between plug-in and EPAgent if it is not formatted correctly (I saw it during MQ disconnect error)
                //resulting in whole EPAgent being shutdown! I'll better comment it, EPA log entry will be sufficient.
                //writeErrorDetectorEntry("MQ Topic Monitor", "Error executing PCF Message: " + (String)arguments[1]);
    			break;

    		case PCF_INQUIRE_TOPIC_STATUS_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Topic Status Time (ms)", ((Long)arguments[1]).longValue());
    			break;

    		case PCF_INQUIRE_TOPIC_STATUS_SUB_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Topic Status Sub Time (ms)", ((Long)arguments[1]).longValue());
    			break;

    		case PCF_INQUIRE_TOPIC_STATUS_PUB_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Topic Status Pub Time (ms)", ((Long)arguments[1]).longValue());
    			break;

    		case PCF_INQUIRE_SUBSCRIPTION_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Subscription Time (ms)", ((Long)arguments[1]).longValue());
    			break;

    		case PCF_INQUIRE_TOPIC_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Topic Time (ms)", ((Long)arguments[1]).longValue());
    			break;

    		case PCF_INQUIRE_TOPIC_NAMES_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability|PCF:Inquire Topic Names (ms)", ((Long)arguments[1]).longValue());
    			break;
    			
    		case MQ_DATA_FETCH_TIME:	
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability:MQ Data Fetch Time (ms)", ((Long)arguments[1]).longValue());
    			break;
    		case MQ_DATA_METRIC_WRITE_TIME:
    			writeLongAverage("MQ Topic Monitor|" + (String)arguments[0] + "|Supportability:MQ Data Write Time (ms)", ((Long)arguments[1]).longValue());
    			break;
    			
    	}
    	
    }
    
    /**
	* Normalize queue manager name so that it can be safely 
	* incorporated into CA APM metric names
	* @param queueManagerName
	* @return
	*/
    protected static final String normalizeQueueManagerName(String queueManagerName) {
        String normalizedName = queueManagerName.replace(':', '-');
        normalizedName = normalizedName.replace('/', '-');
        return normalizedName.trim();
    }	
	

    /**
     * indicates whether configuration value metrics should be reported
     * @return
     */
    private final boolean reportConfValueMetrics(){
    	return RuntimeSettings.REPORT_CONFIGURATION_METRICS_AS_INTEGER_VALUES;
    }
    
    /**
     * Renders status and configuration metrics for administrative topic object. Called from {@link #renderTopicMetrics()}
     * @param atopic Administrative topic object to render
     * @param mPrefix Metric tree prefix determined in {@link #renderTopicMetrics}. This is used to
     * make sure administrative topic is rendered under respective topic node object in metric tree 
     */
    private final void renderATopicMetrics(AdministrativeTopic atopic, String mPrefix){
    	
    	String localVersusClusterPrefix = null;
    	
    	/**
    	 * Determine whether this administrative topic object will be displayed under 'Local Topic' or 'Cluster Topic' folder. Use following rules:
    	 * Display cluster remote topics (i.e. defined on other/remote queue manager) under 'Cluster Topic'
    	 * Display cluster local topics (i.e. defined on this queue manager) under 'Local Topic'
    	 * Display non-cluster/non-shared topics under 'Local Topic'
    	 */
    	if(atopic.getTopicTypeStr().equals("MQTOPT_CLUSTER") && !atopic.getQmgrName().trim().equals(this.queueManager.trim()))
    		localVersusClusterPrefix = TOPIC_CLUSTER;
    	else
    		localVersusClusterPrefix = TOPIC_LOCAL;
    		
		//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges|_AdminTopic|Local Topic|Configuration:
		String mPrefixAdmTopicConfig = mPrefix + ADMIN_TOPIC + localVersusClusterPrefix + TOPIC_CONFIG_NO_UNDERSCORE;
		
		//String mPrefixAdmTopicStatus = mPrefix + ADMIN_TOPIC + localVersusClusterPrefix + TOPIC_STATUS_NO_UNDERSCORE;
		String mPrefixAdmTopicStatus = mPrefixAdmTopicConfig;//let's keep alteration date & time 
															//under '_Configuration' folder as well for simplicity
		
		writeString(mPrefixAdmTopicConfig + "TopicName", atopic.getTopicName());
		writeString(mPrefixAdmTopicConfig + "TopicType", atopic.getTopicTypeStr());
		writeString(mPrefixAdmTopicStatus + "AlterationDate", atopic.getAlterationDate());
		writeString(mPrefixAdmTopicStatus + "AlterationTime", atopic.getAlterationTime());
		writeString(mPrefixAdmTopicConfig + "ClusterName", atopic.getClusterName());
		writeString(mPrefixAdmTopicConfig + "Custom", atopic.getCustom());
		writeString(mPrefixAdmTopicConfig + "DefPersistence", atopic.getDefPersistenceStr());
		writeString(mPrefixAdmTopicConfig + "DefPriority", atopic.getDefPriorityStr());
		writeString(mPrefixAdmTopicConfig + "DefPutResponse", atopic.getDefPutResponseStr());
		writeString(mPrefixAdmTopicConfig + "DurableModelQName", atopic.getDurableModelQName());
		writeString(mPrefixAdmTopicConfig + "DurableSubscriptions", atopic.getDurableSubscriptionsStr());
		writeString(mPrefixAdmTopicConfig + "InhibitPublications", atopic.getInhibitPublicationsStr());
		writeString(mPrefixAdmTopicConfig + "InhibitSubscriptions", atopic.getInhibitSubscriptionsStr());
		writeString(mPrefixAdmTopicConfig + "NonDurableModelQName", atopic.getNonDurableModelQName());
		writeString(mPrefixAdmTopicConfig + "NonPersistentMsgDelivery", atopic.getNonPersistentMsgDeliveryStr());
		writeString(mPrefixAdmTopicConfig + "PersistentMsgDelivery", atopic.getPersistentMsgDeliveryStr());
		writeString(mPrefixAdmTopicConfig + "ProxySubscriptions", atopic.getProxySubscriptionsStr());
		writeString(mPrefixAdmTopicConfig + "PublicationScope", atopic.getPublicationScopeStr());
		writeString(mPrefixAdmTopicConfig + "QMgrName", atopic.getQmgrName());
		writeString(mPrefixAdmTopicConfig + "SubscriptionScope", atopic.getSubscriptionScopeStr());
		writeString(mPrefixAdmTopicConfig + "TopicDesc", atopic.getTopicDesc());
		writeString(mPrefixAdmTopicConfig + "TopicString", atopic.getTopicString());
		writeString(mPrefixAdmTopicConfig + "UseDLQ", atopic.getUseDLQStr());
		writeString(mPrefixAdmTopicConfig + "WildcardOperation", atopic.getWildcardOperationStr());
    	
		if(reportConfValueMetrics()) {
			writeIntCounter(mPrefixAdmTopicConfig + "DefPersistence Value", atopic.getDefPersistence());
			writeIntCounter(mPrefixAdmTopicConfig + "TopicType Value", atopic.getTopicType());
			writeIntCounter(mPrefixAdmTopicConfig + "DefPriority Value", atopic.getDefPriority());
			writeIntCounter(mPrefixAdmTopicConfig + "DefPutResponse Value", atopic.getDefPutResponse());
			writeIntCounter(mPrefixAdmTopicConfig + "DurableSubscriptions Value", atopic.getDurableSubscriptions());
			writeIntCounter(mPrefixAdmTopicConfig + "InhibitPublications Value", atopic.getInhibitPublications());
			writeIntCounter(mPrefixAdmTopicConfig + "InhibitSubscriptions Value", atopic.getInhibitSubscriptions());
			writeIntCounter(mPrefixAdmTopicConfig + "NonPersistentMsgDelivery Value", atopic.getNonPersistentMsgDelivery());
			writeIntCounter(mPrefixAdmTopicConfig + "PersistentMsgDelivery Value", atopic.getPersistentMsgDelivery());
			writeIntCounter(mPrefixAdmTopicConfig + "ProxySubscriptions Value", atopic.getProxySubscriptions());
			writeIntCounter(mPrefixAdmTopicConfig + "PublicationScope Value", atopic.getPublicationScope());
			writeIntCounter(mPrefixAdmTopicConfig + "SubscriptionScope Value", atopic.getSubscriptionScope());
			writeIntCounter(mPrefixAdmTopicConfig + "UseDLQ Value", atopic.getUseDLQ());
			writeIntCounter(mPrefixAdmTopicConfig + "WildcardOperation Value", atopic.getWildcardOperation());    	
		}
		
    }
    
    /**
     * Renders status and configuration metrics for topic node. Called from {@link #generateTopicMetrics()}
     * @param topic
     */
    private final void renderTopicMetrics(Topic topic){
    	
		//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges
		String mPrefix =  MQ_TOPIC_MONITOR + 
				          this.queueManager + 
				          TOPICS_SPACE + 
				          topic.getTopicString().toMetricPath(); 
		
		String mPrefixConfig   = mPrefix + TOPIC_CONFIG;//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges|_Configuration Properties:
		String mPrefixStatus   = mPrefix + TOPIC_STATUS;//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges|_Status:
		
		
		writeString(mPrefixConfig + "TopicString", topic.getTopicString().getTopicString());
		writeString(mPrefixConfig + "Cluster", topic.getCluster());
		writeIntCounter(mPrefixConfig + "DefPriority", topic.getDefPriority());
		writeString(mPrefixConfig + "DefaultPutResponse", topic.getDefaultPutResponseStr());
		writeString(mPrefixConfig + "DefPersistence", topic.getDefPersistenceStr());
		writeString(mPrefixConfig + "DurableSubscriptions", topic.getDurableSubscriptionsStr());
		writeString(mPrefixConfig + "InhibitPublications", topic.getInhibitPublicationsStr());
		writeString(mPrefixConfig + "InhibitSubscriptions", topic.getInhibitSubscriptionsStr());
		writeString(mPrefixConfig + "DurableModelQName", topic.getDurableModelQName());
		writeString(mPrefixConfig + "NonDurableModelQName", topic.getNonDurableModelQName());
		writeString(mPrefixConfig + "PersistentMessageDelivery", topic.getPersistentMessageDeliveryStr());
		writeString(mPrefixConfig + "NonPersistentMessageDelivery", topic.getNonPersistentMessageDeliveryStr());
		writeString(mPrefixConfig + "RetainedPublication", topic.getRetainedPublicationStr());
		writeString(mPrefixConfig + "SubscriptionScope", topic.getSubscriptionScopeStr());
		writeString(mPrefixConfig + "PublicationScope", topic.getPublicationScopeStr());
		writeString(mPrefixConfig + "UseDLQ", topic.getUseDLQStr());

		//writeIntCounter(mPrefixStatus + "PublishCount", topic.getPublishCount());
		//writeIntCounter(mPrefixStatus + "SubscriptionCount", topic.getSubscriptionCount());
		writeLongAverage(mPrefixStatus + "PublishCount", topic.getPublishCount());
		writeLongAverage(mPrefixStatus + "SubscriptionCount", topic.getSubscriptionCount());
		
		
		if(reportConfValueMetrics()) {
			writeIntCounter(mPrefixConfig + "DefaultPutResponse Value", topic.getDefaultPutResponse());
			writeIntCounter(mPrefixConfig + "DefPersistence Value", topic.getDefPersistence());
			writeIntCounter(mPrefixConfig + "DurableSubscriptions Value", topic.getDurableSubscriptions());
			writeIntCounter(mPrefixConfig + "InhibitSubscriptions Value", topic.getInhibitSubscriptions());
			writeIntCounter(mPrefixConfig + "InhibitPublications Value", topic.getInhibitPublications());
			writeIntCounter(mPrefixConfig + "PersistentMessageDelivery Value", topic.getPersistentMessageDelivery());
			writeIntCounter(mPrefixConfig + "RetainedPublication Value", topic.getRetainedPublication());
			writeIntCounter(mPrefixConfig + "NonPersistentMessageDelivery Value", topic.getNonPersistentMessageDelivery());
			writeIntCounter(mPrefixConfig + "SubscriptionScope Value", topic.getSubscriptionScope());
			writeIntCounter(mPrefixConfig + "PublicationScope Value", topic.getPublicationScope());
			writeIntCounter(mPrefixConfig + "UseDLQ Value", topic.getUseDLQ());
		}
		
		
		if(topic.getAdminTopicName()!=null && !topic.getAdminTopicName().trim().equals("")){
			AdministrativeTopic atopic = topic.getAdministrativeTopic();
			renderATopicMetrics(atopic, mPrefix);
		}
		
    }
    
    /**
     * Renders publication metrics. Called from {@link #generateTopicMetrics()}
     * @param topicNow
     * @param topicPrevious
     */
    private final void renderPublicationMetrics(Topic topicNow, Topic topicPrevCycle){
    	
		//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges|_Publications:
		String mPrefix =  MQ_TOPIC_MONITOR + 
				          this.queueManager + 
				          TOPICS_SPACE + 
				          topicNow.getTopicString().toMetricPath() +
				          TOPIC_PUBS;
		
    	if(this.steadyDataManager == null){
			writeLongAverage(mPrefix + NUMBER_OF_ACTIVE_PUBLISHERS, topicNow.getPublisherCount());
		}else{
			this.steadyDataManager.getLongAverageSteadyReporter(mPrefix + NUMBER_OF_ACTIVE_PUBLISHERS).
			writeLongAverage(new Long(topicNow.getPublisherCount()));//implicit auto boxing would create Integer, hence explicit creation of Long is needed 
		}
		
		writeIntCounter(mPrefix + PUBLICATION_MESSAGES_COUNT_TOTAL, topicNow.getPublishedMessagesCount());
 
		
		if(topicPrevCycle != null){
			int delta = Topic.getPublicationMessageCountDelta(topicNow, topicPrevCycle);
			
			if(delta >= 0) 
				writePerIntervalCounter(mPrefix + PUBLICATION_MESSAGES_COUNT_DELTA, delta);
			else /* let's hope this never happens ;-) */
				throw new RuntimeException("renderPublicationMetrics(): Publication delta metric calculated as negative number! topicNow = "+
											topicNow.toString()+" topicPrevCycle = "+topicPrevCycle.toString());
		}
			
    }
    
    /**
     * Renders administrative subscription. Called from {@link #renderSubscriptionMetrics()}
     * @param asubscription
     * @param mPrefix
     * @param topicNow
     * @param topicPrevCycle
     */
    private final void renderAdministrativeSubscription(AdministrativeSubscription asubscription, String mPrefix, Topic topicNow, Topic topicPrevCycle){
    	
		String mPrefix1 = mPrefix + asubscription.getSubNameToMetric()+"|Configuration:";
		String mPrefix2 = mPrefix + asubscription.getSubNameToMetric()+"|Status:";
		
		writeString(mPrefix1 + "AlterationDate", asubscription.getAlterationDate());
		writeString(mPrefix1 + "AlterationTime", asubscription.getAlterationTime());
		
		//retrieve Subscription representing currently iterated AdministrativeSubscription (i.e. currently iterated ADMIN subscription)
		Subscription subscription = topicNow.getSubscriptions().get(asubscription.getSubId());
		int subCount = subscription.getNumberOfMessages();
		
		writeIntCounter(mPrefix2 + SUBSCRIPTION_MESSAGES_COUNT_TOTAL, subCount);
		
		if(topicPrevCycle != null){
			Subscription subscriptionPrevCycle = topicPrevCycle.getSubscriptions().get(asubscription.getSubId());
			
			int delta = 0;
			
			if(subscriptionPrevCycle != null)
				delta = subCount - subscriptionPrevCycle.getNumberOfMessages();
			else
				delta = subCount;
			
			if(delta >=0) 
				writePerIntervalCounter(mPrefix2 + SUBSCRIPTION_MESSAGES_COUNT_DELTA, delta);
			else
				throw new RuntimeException("renderSubscriptionMetrics(): Administrative subscription delta metric calculated as negative number! topicNow = "+
						topicNow.toString()+" topicPrevCycle = "+topicPrevCycle.toString());
			
		}
		
		
		writeString(mPrefix1 + "CreationDate", asubscription.getCreationDate());
		writeString(mPrefix1 + "CreationTime", asubscription.getCreationTime());
		writeString(mPrefix1 + "Destination", asubscription.getDestination());
		writeString(mPrefix1 + "DestinationClass", asubscription.getDestinationClassStr());
		writeString(mPrefix1 + "DestinationCorrelId", asubscription.getDestinationCorrelId());
		writeString(mPrefix1 + "DestinationQueueManager", asubscription.getDestinationQueueManager());
		writeString(mPrefix1 + "Expiry", asubscription.getExpiryStr());
		writeString(mPrefix1 + "PublishedAccountingToken", asubscription.getPublishedAccountingToken());
		writeString(mPrefix1 + "PublishedApplicationIdentityData", asubscription.getPublishedApplicationIdentityData());
		writeString(mPrefix1 + "PublishPriority", asubscription.getPublishPriorityStr());
		writeString(mPrefix1 + "PublishSubscribeProperties", asubscription.getPublishSubscribePropertiesStr());
		writeString(mPrefix1 + "RequestOnly", asubscription.getRequestonlyStr());
		writeString(mPrefix1 + "Selector", asubscription.getSelector());
		writeString(mPrefix1 + "SelectorType", asubscription.getSelectorTypeStr());
		writeIntCounter(mPrefix1 + "SubscriptionLevel", asubscription.getSubscriptionLevel());
		writeString(mPrefix1 + "SubscriptionScope", asubscription.getSubscriptionScopeStr());
		writeString(mPrefix1 + "SubscriptionType", asubscription.getSubscriptionTypeStr());
		writeString(mPrefix1 + "SubscriptionUser", asubscription.getSubscriptionUser());
		writeString(mPrefix1 + "TopicObject", asubscription.getTopicObject());
		writeString(mPrefix1 + "TopicString", asubscription.getTopicString());
		writeString(mPrefix1 + "Userdata", asubscription.getUserdata());
		writeString(mPrefix1 + "VariableUser", asubscription.getVariableUserStr());
		writeString(mPrefix1 + "WildcardSchema", asubscription.getWildcardSchemaStr());
		
    	
		
		if(reportConfValueMetrics()) {
			writeIntCounter(mPrefix1 + "DestinationClass Value", asubscription.getDestinationClass());
			writeIntCounter(mPrefix1 + "Expiry Value", asubscription.getExpiry());
			writeIntCounter(mPrefix1 + "PublishPriority Value", asubscription.getPublishPriority());
			writeIntCounter(mPrefix1 + "PublishSubscribeProperties Value", asubscription.getPublishSubscribeProperties());
			writeIntCounter(mPrefix1 + "RequestOnly Value", asubscription.getRequestonly());
			writeIntCounter(mPrefix1 + "SelectorType Value", asubscription.getSelectorType());
			writeIntCounter(mPrefix1 + "SubscriptionScope Value", asubscription.getSubscriptionScope());
			writeIntCounter(mPrefix1 + "SubscriptionType Value", asubscription.getSubscriptionType());
			writeIntCounter(mPrefix1 + "VariableUser Value", asubscription.getVariableUser());
			writeIntCounter(mPrefix1 + "WildcardSchema Value", asubscription.getWildcardSchema());			
		}
    	
    }
    
    /**
     *
     * Renders administrative subscription top level summary metrics. 
     * Called from {@link #renderSubscriptionMetrics()}
     *
     * @param topicNow
     * @param topicPrevCycle
     * @param mPrefix
     * @param subscriptionType
     */
    private final void renderSubscriptionSumamryMetrics(Topic topicNow, Topic topicPrevCycle, String mPrefix, SubscriptionType subscriptionType){
    	
		/**
		 *write same high level metrics as we do for publishers in {@link #renderPublicationMetrics} 
		 */

    	if(this.steadyDataManager == null){
			writeLongAverage(mPrefix + NUMBER_OF_ACTIVE_SUBSCRIBERS, topicNow.getSubscriberCount(subscriptionType));
		}else{
			this.steadyDataManager.getLongAverageSteadyReporter(mPrefix + NUMBER_OF_ACTIVE_SUBSCRIBERS).
			writeLongAverage(new Long(topicNow.getSubscriberCount(subscriptionType)));//implicit auto boxing would create Integer, hence explicit creation of Long is needed 
		}
		
		writeIntCounter(mPrefix + SUBSCRIPTION_MESSAGES_COUNT_TOTAL, topicNow.getSubscribedMessagesCount(subscriptionType));
    	
		
		if(topicPrevCycle != null){
			int delta = Topic.getSubscriptionMessageCountDelta(topicNow, topicPrevCycle, subscriptionType);
			if(delta >= 0) 
				writePerIntervalCounter(mPrefix + SUBSCRIPTION_MESSAGES_COUNT_DELTA, delta);
			else /* let's hope this never happens ;-) */
				throw new RuntimeException("renderSubscriptionMetrics(): Subscription delta metric calculated as negative number! topicNow = "+
											topicNow.toString()+" topicPrevCycle = "+topicPrevCycle.toString());
		}
    	
    	
    }
    
    /**
     * Renders subscription metrics. Called from {@link #generateTopicMetrics()}
     * @param topicNow
     * @param topicPrevCycle
     */
    private final void renderSubscriptionMetrics(Topic topicNow, Topic topicPrevCycle){
    	
		//e.g. MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|Oranges
		String mPrefix =  MQ_TOPIC_MONITOR + 
				          this.queueManager + 
				          TOPICS_SPACE + 
				          topicNow.getTopicString().toMetricPath();
				          
		
		String mPrefix1 = mPrefix + TOPIC_SUBS;
		String mPrefix2 = mPrefix + TOPIC_ADMIN_SUBS;
		
		String mPrefix3 = mPrefix + TOPIC_API_SUBS;
		String mPrefix4 = mPrefix + TOPIC_PROXY_SUBS;
		String mPrefix5 = mPrefix + TOPIC_ADMIN_SUBS2;

		
		/**
		 * Render summary metrics: 
		 * -Number of Active Subscribers
		 * -Number of Messages Per Interval
		 * -Number of Messages Total
		 * 
		 * on appropriate levels, i.e. for ALL subscriptions and for API, PROXY and ADMIN separately 
		 * 
		 */
		renderSubscriptionSumamryMetrics(topicNow, topicPrevCycle, mPrefix1, SubscriptionType.ALL);
		renderSubscriptionSumamryMetrics(topicNow, topicPrevCycle, mPrefix3, SubscriptionType.API);
		renderSubscriptionSumamryMetrics(topicNow, topicPrevCycle, mPrefix4, SubscriptionType.PROXY);
		renderSubscriptionSumamryMetrics(topicNow, topicPrevCycle, mPrefix5, SubscriptionType.ADMIN);
		
		/**
		 * render metrics (both Config & Status) for Administrative subscriptions 
		 */
		for(AdministrativeSubscription asubscription : topicNow.getAdministrativeSubscriptions()) 
			renderAdministrativeSubscription(asubscription, mPrefix2, topicNow, topicPrevCycle);
    	
    }
    
    /**
     * Main method responsible for rendering topic metrics. Method takes as an input metrics of both current and previous cycle.
     * Previous cycle will be null for the very first run/execution of this method.If so, delta metrics are not calculated. 
     * If topicsPreviousCycle is available delta metrics for publications and subscriptions can be calculated.
     * 
     * @param topicsCurrentCycle - container holding topics (and associated publications and subscriptions) of current reporting cycle
     * @param topicsPreviousCycle - container holding topics (and associated publications and subscriptions) of previous reporting cycle
     * @return
     */
    public final void generateTopicMetrics(Topics topicsCurrentCycle, Topics topicsPreviousCycle){
    	
    	for(Map.Entry<TopicString, Topic> entry : topicsCurrentCycle.getTopics().entrySet()){
    		
    		Topic iteratedTopic =  entry.getValue();
    		Topic iteratedTopicPrevCycle = null;
    		
    		if(topicsPreviousCycle != null)
    				iteratedTopicPrevCycle = topicsPreviousCycle.
    												 getTopics().
    												 get(iteratedTopic.getTopicString());
    															
			renderTopicMetrics(iteratedTopic);
			renderPublicationMetrics(iteratedTopic, iteratedTopicPrevCycle);
			renderSubscriptionMetrics(iteratedTopic, iteratedTopicPrevCycle);
    		
    	}    	
    	
    }
    
    /**
     *	Abstract methods that need to be implemented by descendants of this class 
     */
    
	abstract public void writeTimestamp(String metric, Date value);

	abstract public void writeString(String metric, String value);
	
	abstract public void writeLongAverage(String metric, long value);

	abstract public void writeErrorDetectorEntry(String subject, String errorEntry);

	abstract public void writePerIntervalCounter(String metric);
	
	abstract public void writePerIntervalCounter(String metric, int value);

	abstract public void forceExistPerIntervalCounter(String metric);

	abstract public void forceExistLongIntervalCounter(String metric);

	abstract public void writeLongCounter(String metric, long value);

	abstract public void writeIntCounter(String metric, int value);

	abstract public void decrementCounter(String metric, int value);

	abstract public void incrementCounter(String metric, int value);
	
	
}
