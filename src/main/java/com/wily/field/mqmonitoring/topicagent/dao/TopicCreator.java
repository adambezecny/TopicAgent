package com.wily.field.mqmonitoring.topicagent.dao;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.PCFMessage;

import java.io.IOException;

import com.wily.field.mqmonitoring.topicagent.entity.*;

/**
 * 
 * This class is responsible for creation of Topic entity.
 * Purpose of this class is to make MQDAO more lightweight and 
 * include all logic of entity creation (Topic) into separate class.
 * 
 * @author Adam Bezecny - CA Services
 *
 */
class TopicCreator {

	protected static Topic create(PCFMessage pcfMessage) throws IOException, MQException {
		
		   String topicStringStr = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim();
		   TopicString topicString = new TopicString(topicStringStr);
		   
		   String cluster = pcfMessage.getStringParameterValue(CMQC.MQCA_CLUSTER_NAME).trim();
		  
		   int defPriority = pcfMessage.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY); 
		   
		   int defaultPutResponse = pcfMessage.getIntParameterValue(CMQC.MQIA_DEF_PUT_RESPONSE_TYPE);
		   String defaultPutResponseStr;
		   if(defaultPutResponse == CMQC.MQPRT_ASYNC_RESPONSE)
			   defaultPutResponseStr = "MQPRT_ASYNC_RESPONSE";
		   else
			   defaultPutResponseStr = "MQPRT_SYNC_RESPONSE";
		   
		   
		   int defPersistence = pcfMessage.getIntParameterValue(CMQC.MQIA_TOPIC_DEF_PERSISTENCE);
		   String defPersistenceStr;
		   if(defPersistence == CMQC.MQPER_PERSISTENT) 
				defPersistenceStr="MQPER_PERSISTENT";
		   else
				defPersistenceStr="MQPER_NOT_PERSISTENT";
		   
		   
		   int durableSubscriptions = pcfMessage.getIntParameterValue(CMQC.MQIA_DURABLE_SUB);
		   String durableSubscriptionsStr;
		   if(durableSubscriptions == CMQC.MQSUB_DURABLE_ALLOWED)
			   durableSubscriptionsStr = "MQSUB_DURABLE_ALLOWED";
		   else
			   durableSubscriptionsStr = "MQSUB_DURABLE_INHIBITED";
		   
		   int inhibitPublications = pcfMessage.getIntParameterValue(CMQC.MQIA_INHIBIT_PUB);
		   String inhibitPublicationsStr;
		   if(inhibitPublications == CMQC.MQTA_PUB_INHIBITED)
			   inhibitPublicationsStr = "MQTA_PUB_INHIBITED";
		   else
			   inhibitPublicationsStr = "MQTA_PUB_ALLOWED";
		   
		   
		   int inhibitSubscriptions = pcfMessage.getIntParameterValue(CMQC.MQIA_INHIBIT_SUB);
		   String inhibitSubscriptionsStr;
		   if(inhibitSubscriptions == CMQC.MQTA_SUB_INHIBITED)
			   inhibitSubscriptionsStr = "MQTA_SUB_INHIBITED";
		   else
			   inhibitSubscriptionsStr = "MQTA_SUB_ALLOWED";	   
		   
		   
		   
		   String adminTopicName = pcfMessage.getStringParameterValue(CMQC.MQCA_ADMIN_TOPIC_NAME);
		   String durableModelQName = pcfMessage.getStringParameterValue(CMQC.MQCA_MODEL_DURABLE_Q); 
		   String nonDurableModelQName = pcfMessage.getStringParameterValue(CMQC.MQCA_MODEL_NON_DURABLE_Q); 
		   
		   int persistentMessageDelivery = pcfMessage.getIntParameterValue(CMQC.MQIA_PM_DELIVERY); 
		   String persistentMessageDeliveryStr;
		   if(persistentMessageDelivery == CMQC.MQDLV_ALL)
			   persistentMessageDeliveryStr = "MQDLV_ALL";
		   else if(persistentMessageDelivery == CMQC.MQDLV_ALL_DUR)
			   persistentMessageDeliveryStr = "MQDLV_ALL_DUR";
		   else
			   persistentMessageDeliveryStr = "MQDLV_ALL_AVAIL";	   
		   
		   
		   int nonPersistentMessageDelivery = pcfMessage.getIntParameterValue(CMQC.MQIA_NPM_DELIVERY);
		   String nonPersistentMessageDeliveryStr;
		   if(nonPersistentMessageDelivery == CMQC.MQDLV_ALL)
			   nonPersistentMessageDeliveryStr = "MQDLV_ALL";
		   else if(nonPersistentMessageDelivery == CMQC.MQDLV_ALL_DUR)
			   nonPersistentMessageDeliveryStr = "MQDLV_ALL_DUR";
		   else
			   nonPersistentMessageDeliveryStr = "MQDLV_ALL_AVAIL";
		   
		   
		   int retainedPublication = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_RETAINED_PUBLICATION);
		   String retainedPublicationStr;
		   if(retainedPublication == CMQCFC.MQQSO_YES)
			   retainedPublicationStr = "MQQSO_YES";
		   else
			   retainedPublicationStr = "MQQSO_NO";
		   
		   int publishCount = pcfMessage.getIntParameterValue(CMQC.MQIA_PUB_COUNT);
		   
		   int subscriptionScope = pcfMessage.getIntParameterValue(CMQC.MQIA_SUB_SCOPE);
		   String subscriptionScopeStr;
		   if(subscriptionScope == CMQC.MQSCOPE_ALL)
			   subscriptionScopeStr = "MQSCOPE_ALL";
		   else
			   subscriptionScopeStr = "MQSCOPE_QMGR";	   
		   
		   
		   int subscriptionCount = pcfMessage.getIntParameterValue(CMQC.MQIA_SUB_COUNT);
		   
		   int publicationScope = pcfMessage.getIntParameterValue(CMQC.MQIA_PUB_SCOPE);
		   String publicationScopeStr;
		   if(publicationScope == CMQC.MQSCOPE_ALL)
			   publicationScopeStr = "MQSCOPE_ALL";
		   else
			   publicationScopeStr = "MQSCOPE_QMGR";
		   
		   
		   int useDLQ = pcfMessage.getIntParameterValue(CMQC.MQIA_USE_DEAD_LETTER_Q);
		   String useDLQStr;
		   if(useDLQ == CMQC.MQUSEDLQ_NO)
			   useDLQStr = "MQUSEDLQ_NO";
		   else
			   useDLQStr = "MQUSEDLQ_YES";
		   
		   
		   Topic topic = new Topic(topicString, cluster, defPriority,
					defaultPutResponse,
					defaultPutResponseStr, defPersistence,
					defPersistenceStr, durableSubscriptions,
					durableSubscriptionsStr, inhibitPublications,
					inhibitPublicationsStr, inhibitSubscriptions,
					inhibitSubscriptionsStr, adminTopicName,
					durableModelQName, nonDurableModelQName,
					persistentMessageDelivery, persistentMessageDeliveryStr,
					nonPersistentMessageDelivery,
					nonPersistentMessageDeliveryStr, retainedPublication,
					retainedPublicationStr, publishCount,
					subscriptionScope,
					subscriptionScopeStr, subscriptionCount,
					publicationScope,
					publicationScopeStr, useDLQ, useDLQStr);

		   return topic;
		
	}	
	
}
