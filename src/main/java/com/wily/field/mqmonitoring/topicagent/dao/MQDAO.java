package com.wily.field.mqmonitoring.topicagent.dao;


import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.MQCFIN;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

import java.util.Hashtable;
import java.io.IOException;

import com.wily.field.mqmonitoring.topicagent.entity.*;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;
import com.wily.field.mqmonitoring.topicagent.metricwriter.SupportabilityMetrics;

import static com.wily.introscope.epagent.EPALogger.*;

/**
 * MQ Data Access Object. This object encapsulates all low level communication
 * with MQ via PCF messages. For more information about PCF messages see links below:
 * 
 * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.adm.doc/q019990_.htm
 * http://www.ibm.com/support/knowledgecenter/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q030980_.htm
 * https://www.ibm.com/support/knowledgecenter/SSFKSJ_7.5.0/com.ibm.mq.javadoc.doc/WMQJavaClasses/com/ibm/mq/pcf/package-summary.html
 * 
 * @author Adam Bezecny -  CA Services
 * @author Andreas Reiss - CA APM Global SWAT Team
 *
 */
public class MQDAO {

	private static final int EMPTY_QUEUE = 2033;
    private final MQQueueManager mqQueueManager;
    private final MQProperties mQProperties;
    private MetricWriter metricWriter; 
    public enum TopicStatusType {TopicStatus, TopicPub, TopicSub}
    
    /**
     * Initiates connection with MQ manager. 
     * @param mQProperties MQ manager to be connected to connection details
     * @param metricWriter metricWriter used to write supportability metrics
     * @throws MQException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })  //this is to prevent warnings for connectProperties Hashtable in constructor
	public MQDAO(MQProperties mQProperties, MetricWriter metricWriter) throws MQException {
		
        this.mQProperties = mQProperties;		
        this.metricWriter = metricWriter;
        
        
		Hashtable connectProperties = new Hashtable();
        
        connectProperties.put(CMQC.HOST_NAME_PROPERTY, mQProperties.getHost());
        connectProperties.put(CMQC.PORT_PROPERTY, new Integer(mQProperties.getPort()));
        connectProperties.put(CMQC.CHANNEL_PROPERTY, mQProperties.getChannel());
        connectProperties.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
         
        if (mQProperties.isPasswordSet()) {
            connectProperties.put(CMQC.USER_ID_PROPERTY, mQProperties.getUser());
            connectProperties.put(CMQC.PASSWORD_PROPERTY, mQProperties.getPassword());
        }
        
        mqQueueManager = new MQQueueManager(mQProperties.getQueueManager(), connectProperties);
        MQException.logExclude(CMQCFC.MQRCCF_TOPIC_STRING_NOT_FOUND);        
		
	}	

    /**
     * Use this method to terminate connection with MQ manager once communication is over.
     */
    public void disconnect() {
      
    	try {
            epainfo("Disconnecting from Queue Manager " + mQProperties.getQueueManager() + " on Host " + mQProperties.getHost());
            mqQueueManager.disconnect();
            epainfo("Successfully disconnected from Queue Manager");
        } catch (MQException e) {
            epaerror("Error disconnecting from Queue Manager " + mQProperties.getQueueManager() + ": " + e.getMessage());
            metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_DISCONNECT_ERROR, mQProperties.getQueueManager(), e.getMessage());
        }
    }
    
    /**
     * Used by sample publishers and subscribers, otherwise this is internal object
     * and should not be used outside this class!
     * @return
     */
    public MQQueueManager getMQQueueManager(){
    	return this.mqQueueManager;
    }
    
    /**
     * low level generic method for sending PCFMessage request and returning response PCFMessage
     * @param pcfMessageReq request PCF message
     * @return PCF messages representing response to PCF request 
     * @throws IOException
     * @throws MQException
     */
    private PCFMessage[] doMQRequest(PCFMessage pcfMessageReq) throws IOException, MQException {
           
    	    PCFMessageAgent agent = new PCFMessageAgent(mqQueueManager);
            PCFMessage[] responses;
            
            try {
                long startTime = System.currentTimeMillis();
                responses = agent.send(pcfMessageReq);
                long duration = System.currentTimeMillis() - startTime;

                metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_SEND_MESSAGE_TIME, mQProperties.getQueueManager(), duration);
                
            } catch (MQException e) {
                if (e.completionCode == EMPTY_QUEUE || e.reasonCode == CMQCFC.MQRCCF_TOPIC_STRING_NOT_FOUND) {
                    return new PCFMessage[0];
                } else {
                	metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_SEND_MESSAGE_ERROR, mQProperties.getQueueManager(), e.getMessage());
                    throw e;
                }
            } finally {
                agent.disconnect();
            }
            return responses;
        }    
 
    
    
    
    /**
     * Calls PCF "Inquire Topic Status", for details see:
     * 
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088140_.htm
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088150_.htm?view=kc
     * https://www.ibm.com/support/knowledgecenter/SSFKSJ_7.5.0/com.ibm.mq.javadoc.doc/WMQJavaClasses/com/ibm/mq/pcf/package-summary.html
     * 
     * PCF is called with option MQIACF_TOPIC_SUB, i.e. retrieves subscription data for specified topics (#)
     * 
     * @return PCF messages representing topics' subscriptions
     * @throws IOException
     * @throws MQException
     */
    protected PCFMessage[] pcfInquireTopicStatusSub() throws IOException, MQException{
    	
    	long startTime = System.currentTimeMillis();
    	
    	PCFMessage pcfMessageReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC_STATUS);
    	pcfMessageReq.addParameter(CMQC.MQCA_TOPIC_STRING, "#");
    	pcfMessageReq.addParameter(new MQCFIN(CMQCFC.MQIACF_TOPIC_STATUS_TYPE, CMQCFC.MQIACF_TOPIC_SUB));    	
    	
    	PCFMessage[] responses = doMQRequest(pcfMessageReq);
    	
    	long finishTime = System.currentTimeMillis() - startTime;
    	metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_TOPIC_STATUS_SUB_TIME, mQProperties.getQueueManager(), finishTime);
        
        return responses;
    }
    

    /**
     * Calls PCF "Inquire Topic Status", for details see:
     * 
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088140_.htm
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088150_.htm?view=kc
     * https://www.ibm.com/support/knowledgecenter/SSFKSJ_7.5.0/com.ibm.mq.javadoc.doc/WMQJavaClasses/com/ibm/mq/pcf/package-summary.html
     * 
     * PCF is called with option MQIACF_TOPIC_PUB, i.e. retrieves publication data for specified topics (#)
     * 
     * @return PCF messages representing topics' subscriptions
     * @throws IOException
     * @throws MQException
     */
    protected PCFMessage[] pcfInquireTopicStatusPub() throws IOException, MQException {

        long startTime = System.currentTimeMillis();

        PCFMessage pcfMessageReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC_STATUS);
        pcfMessageReq.addParameter(CMQC.MQCA_TOPIC_STRING, "#");
        pcfMessageReq.addParameter(new MQCFIN(CMQCFC.MQIACF_TOPIC_STATUS_TYPE, CMQCFC.MQIACF_TOPIC_PUB));
        
        PCFMessage[] responses = doMQRequest(pcfMessageReq);

        long finishTime = System.currentTimeMillis() - startTime;
        
        metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_TOPIC_STATUS_PUB_TIME, mQProperties.getQueueManager(), finishTime);

        return responses;
    }
    
    /**
     * Return all nodes in topic tree via PCF "Inquire Topic Status"
     * @return
     * @throws IOException
     * @throws MQException
     */
    protected PCFMessage[] pcfInquireTopicStatus() throws IOException, MQException {

        long startTime = System.currentTimeMillis();

        PCFMessage pcfMessageReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC_STATUS);
        pcfMessageReq.addParameter(CMQC.MQCA_TOPIC_STRING, "#");
        
        
        PCFMessage[] responses = doMQRequest(pcfMessageReq);

        long finishTime = System.currentTimeMillis() - startTime;
        
        metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_TOPIC_STATUS_TIME, mQProperties.getQueueManager(), finishTime);
        return responses;
    	
    	
    }
    
    /**
     * Calls PCF "Inquire Subscription", for details see 
     * 
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088040_.htm
     * http://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.ref.adm.doc/q088050_.htm
     * https://www.ibm.com/support/knowledgecenter/SSFKSJ_7.5.0/com.ibm.mq.javadoc.doc/WMQJavaClasses/com/ibm/mq/pcf/package-summary.html
     * 
     * We need to call this function in order to retrieve subscription name. Most of subscription data is retrieved by calling {@link #retrieveTopicSubscriptionStatus} 
     * utilizing PCF "Inquire Topic Status" which unfortunately does not provide subscription name attribute 
     * 
     * @param subscriptionId - ID of subscription to be retrieved
     * @return PCF message representing retrieved subscription
     * @throws IOException
     * @throws MQException
     */
    protected PCFMessage[] pcfInquireSubscription(byte[] subscriptionId) throws IOException, MQException{
    	
    	long startTime = System.currentTimeMillis();
    	
    	PCFMessage pcfMessageReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_SUBSCRIPTION);
    	pcfMessageReq.addParameter(CMQCFC.MQBACF_SUB_ID, subscriptionId);
  
    	
    	PCFMessage[] responses = doMQRequest(pcfMessageReq);
    	
    	long finishTime = System.currentTimeMillis() - startTime;
    	metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_SUBSCRIPTION_TIME, mQProperties.getQueueManager(), finishTime);
        return responses;
    }     
    

    /**
     * Wraps PCF call 'Inquire Topic'
     * @param topicName - topic name of administrative topic object to be returned
     * @return 3 possible outcomes:
     * 		   (a) 1x PCF message for local & non-shared/clustered topics, 
     *         (b) 2x PCF messages for cluster topic (when called on QM where this cluster topic is defined locally).
     *             (1st message (MQIA_TOPIC_TYPE = MQTOPT_LOCAL/0), 2nd message - (MQIA_TOPIC_TYPE = MQTOPT_CLUSTER/1))
     *          
     *         (c) 1x PC message (MQIA_TOPIC_TYPE = MQTOPT_CLUSTER/1) for cluster topic when called from remote QM
     *         
     * First message (for local topic) contains following attributes not contained in second message:
     * 	MQIA_MULTICAST
	 *	MQCA_COMM_INFO_NAME
	 *	MQIA_USE_DEAD_LETTER_Q
	 *	MQCA_CUSTOM
     *
     * Second message (for cluster topic) contains following attributes not contained in first message:
     * 	MQCA_CLUSTER_Q_MGR_NAME
	 *	MQCA_Q_MGR_IDENTIFIER
	 *	MQCA_CLUSTER_DATE
	 *	MQCA_CLUSTER_TIME
     * 
     * First of the messages represents local topic (MQIA_TOPIC_TYPE = MQTOPT_LOCAL/0), second message
     * represents cluster topic (MQIA_TOPIC_TYPE = MQTOPT_CLUSTER/1)
     * 
     * @throws IOException
     * @throws MQException
     */
	protected PCFMessage[] pcfInquireTopic(String topicName) throws IOException, MQException {

    	long startTime = System.currentTimeMillis();
    	
    	PCFMessage inquireTopic = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC);
    	inquireTopic.addParameter(CMQC.MQCA_TOPIC_NAME, topicName);
    	
    	/**
    	 *  We require cluster information for the topic. If it is cluster topic two PCFMessage objects are returned.
    	 *  For local topics only single PCFMessage object is returned.
    	 */
    	inquireTopic.addParameter(new MQCFIN(CMQCFC.MQIACF_CLUSTER_INFO, CMQCFC.MQFC_YES));
    	
    	PCFMessage[] responses = doMQRequest(inquireTopic);
    	
    	long finishTime = System.currentTimeMillis() - startTime;

    	metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_TOPIC_TIME, mQProperties.getQueueManager(), finishTime);
    	
    	return responses;
    }
    
    /**
     * Wraps PCF call "Inquire Topic Names"
     * @param topicName
     * @return
     * @throws IOException
     * @throws MQException
     */
	protected PCFMessage[] pcfInquireTopicNames(String topicName) throws IOException, MQException {
    	
    	long startTime = System.currentTimeMillis();
    	
        PCFMessage pcfMessageReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC_NAMES);
        pcfMessageReq.addParameter(CMQC.MQCA_TOPIC_NAME, topicName);
        PCFMessage[] responses = doMQRequest(pcfMessageReq);
    	
    	long finishTime = System.currentTimeMillis() - startTime;

    	metricWriter.writeSupportabilityMetric(SupportabilityMetrics.PCF_INQUIRE_TOPIC_NAMES_TIME, mQProperties.getQueueManager(), finishTime);
    	
    	return responses;
    	
    }
    
    
    /**
     * Retrieves names of all topics defined on queue manager
     * @return array of topic names. Each name is right padded with blank characters (spaces) to fixed length. 
     * When displaying topic names as metrics this must be taken into account and topic names must be trimmer!
     * @throws IOException
     * @throws MQException
     */
    protected String[] getTopicNames() throws IOException, MQException {
    	String[] topicNames = null;
        PCFMessage[] responses = pcfInquireTopicNames("*");
        if(responses.length ==1) topicNames = responses[0].getStringListParameterValue(CMQCFC.MQCACF_TOPIC_NAMES);
    	return topicNames;
    }
    
    
    
    
    /**
     * Enhances topics passed in "topics" parameter with publication data.
     * Publications are retrieved in batch call in order to prevent multiple calls (per each topic)
     * Once publications are retrieved they are iterated over and associated with topics (based on topicString)
     * See also {@link #addSubscriptions} which is doing same thing for topic subscriptions
     * @param topics In/Out parameter with topics. Topics passed in this parameter are enhanced with publication data
     * @throws IOException
     * @throws MQException
     */
    private void addPublications(Topics topics) throws IOException, MQException {

 	   	//retrieve topic publications as PCF messages
 	   	PCFMessage[] pcfMessages = pcfInquireTopicStatusPub();
 	   	
 	   	for(PCFMessage pcfMessage : pcfMessages)/* iterate over each PCF message representing topic publication */{
 	   		
 	   		TopicString topicString = new TopicString(pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim());
 	   		
 	   	    //filter out SYSTEM publications and any potential publications to empty topic string, i.e. to SYSTEM.BASE.TOPIC
 	   	    if(topicString.getTopicString().startsWith("SYSTEM.") || topicString.getTopicString().trim().equals("")) continue; 
 	   																   
 	   		Publication topicPublication = PublicationCreator.create(pcfMessage);
 	   		topics.getTopicByTopicString(topicString.getTopicString()).addOrUpdatePublication(topicPublication);
 	   		
 	   	}	   
 		   
    }  
    
    
    /**
     * Enhances topics passed in "topics" parameter with subscription data.
     * Subscriptions are retrieved in batch call in order to prevent multiple calls (per each topic)
     * Once subscriptions are retrieved they are iterated over and associated with topics (based on topicString)
     * See also {@link #addPublications} which is doing same thing for topic publications
     * @param topics In/Out parameter with topics. Topics passed in this parameter are enhanced with subscription data
     * @throws IOException
     * @throws MQException
     */
   private void addSubscriptions(Topics topics) throws IOException, MQException {

	   	//retrieve topic subscriptions 
	   	PCFMessage[] pcfMessages = pcfInquireTopicStatusSub();
	   	
	   	for(PCFMessage pcfMessage : pcfMessages){
	   		
	   		TopicString topicString = new TopicString(pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim());
	   		
	   		//filter out system subscriptions and any potential subscriptions to empty topic string, i.e. to SYSTEM.BASE.TOPIC
	   		if(topicString.getTopicString().startsWith("SYSTEM.") || topicString.getTopicString().trim().equals("")) continue;
	   		
	   		int subscriptionType = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUB_TYPE);
	   		byte[] subscriptionId = pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_SUB_ID);
	   		
	   		Subscription subscription = null;
	   		AdministrativeSubscription asubscription = null;
	   		
	   		/**
	   		 * for all subscriptions we are calling getUnwildcardedParentTopic() instead of getTopicString()!
	   		 * This is to attach subscription to uppermost topic in topic tree in case of wild-carded subscriptions, i.e.
	   		 * subscription with topic string A/B/+/C will be attached to topic node A/B!
	   		 */
	   		if(subscriptionType == CMQCFC.MQSUBTYPE_ADMIN)/* ADMIN subscription */{
	   			asubscription = getAdministrativeSubscription(subscriptionId);
	   			topics.getTopicByTopicString(topicString.getUnwildcardedParentTopic()).addAdministrativeSubscription(asubscription);
	   		}	
	   		
	   		//for all subscriptions add it to subscription map (even ADMIN subscription processed above!)
	   		subscription = SubscriptionCreator.create(pcfMessage);
	   		topics.getTopicByTopicString(topicString.getUnwildcardedParentTopic()).addSubscription(subscription);
	   		
	   		
	   	}	   
	   
   }
   
   /**
    * Returns administrative topic object.
    * See also {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter#renderATopicMetrics()}
    * in order to understand why we return cluster topic if we receive two PCF messages 
    * (it is used to determine metric prefix 'Local Topics' versus 'Cluster Topics')
    * @param topicName - Name of the administrative topic to retrieve
    * @return
    * @throws IOException
    * @throws MQException
    */
   protected AdministrativeTopic getAdministrativeTopic(String topicName) throws IOException, MQException {
	   
	   PCFMessage[] pcfMessages = pcfInquireTopic(topicName);
	   
	   if(pcfMessages.length==1)/*if we got just one message this is either local & non-shared/non-cluster topic  or clustered & remote topic*/{
		   return AdministrativeTopicCreator.create(pcfMessages[0]);   
	   }else/*if we have two messages one is for local topic, the other one is for cluster topic, i.e. we are processing clustered and locally defined topic*/{
		   /***
		    * in this case we need to return cluster topic, see {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter#renderATopicMetrics()}
		    */
		   if(pcfMessages[0].getIntParameterValue(CMQC.MQIA_TOPIC_TYPE) == CMQC.MQTOPT_CLUSTER)
			   return AdministrativeTopicCreator.create(pcfMessages[0]);
		   else   
			   return AdministrativeTopicCreator.create(pcfMessages[1]);
	   }
	   
	   
   }

   /**
    * 
    * @param subscriptionId
    * @return
    * @throws IOException
    * @throws MQException
    */
   protected AdministrativeSubscription getAdministrativeSubscription(byte[] subscriptionId) throws IOException, MQException {
	   PCFMessage pcfMessage = pcfInquireSubscription(subscriptionId)[0];
	   return AdministrativeSubscriptionCreator.create(pcfMessage);
   }
   
   /**
    * 
    * @return
    * @throws IOException
    * @throws MQException
    */
   protected Topics getTopics() throws IOException, MQException {
	
	   Topics topicSpace = new Topics();
	   
	   PCFMessage[] pcfMessages = pcfInquireTopicStatus();
	   
	   for(PCFMessage pcfMessage : pcfMessages){
		   
		   String topicStringStr = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim();
		   
		   //filter out root topic (SYSTEM.BASE.TOPIC) and all system topics
		   if(topicStringStr.startsWith("SYSTEM.") || topicStringStr.trim().equals("") ) continue;
		   
		   Topic topic = TopicCreator.create(pcfMessage);
		   
		   if(!topic.getAdminTopicName().trim().equals("")){
			   AdministrativeTopic atopic = getAdministrativeTopic(topic.getAdminTopicName());
			   topic.setAdministrativeTopic(atopic);
		   }	   
		   
		   topicSpace.addTopic(topic);
	   }
	   
	   return topicSpace;
	   
   }
   
    
   /**
    * Retrieves topics and their subscriptions and publications
    * @return
    * @throws IOException
    * @throws MQException
    */
    public Topics retrieveTopicSpace() throws IOException, MQException {
    
    	Topics topicSpace = new Topics();
    	
    	topicSpace = getTopics();
    	
    	addSubscriptions(topicSpace);//retrieve all subscriptions and associate them to corresponding Topic objects
    	addPublications(topicSpace);//retrieve all publications and associate them to corresponding Topic objects
    			
    	return topicSpace;
    }
    
}
