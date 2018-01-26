package com.wily.field.mqmonitoring.topicagent.dao;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.pcf.PCFMessage;
import java.io.IOException;
import com.wily.field.mqmonitoring.topicagent.entity.*;

/**
 * 
 * This class is responsible for creation of AdministrativeTopic entity.
 * Purpose of this class is to make MQDAO more lightweight and 
 * include all logic of entity creation (AdministrativeTopic) into separate class.
 * 
 * See comment in {@link com.wily.field.mqmonitoring.topicagent.dao.MQDAO#pcfInquireTopic()}
 *
 * @author Adam Bezecny - CA Services
 *
 */
class AdministrativeTopicCreator {

	   protected static AdministrativeTopic create(PCFMessage pcfMessage) throws IOException, MQException {
		   
		   String topicName = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
		   
		   int topicType = pcfMessage.getIntParameterValue(CMQC.MQIA_TOPIC_TYPE); 
		   String topicTypeStr;
		   if(topicType == CMQC.MQTOPT_LOCAL)
				topicTypeStr = "MQTOPT_LOCAL";
		   else
				topicTypeStr = "MQTOPT_CLUSTER";
		   
		   String alterationDate = pcfMessage.getStringParameterValue(CMQC.MQCA_ALTERATION_DATE); 
		   String alterationTime = pcfMessage.getStringParameterValue(CMQC.MQCA_ALTERATION_TIME);
		   String clusterName = pcfMessage.getStringParameterValue(CMQC.MQCA_CLUSTER_NAME);
		   
		   String custom = "";
		   if(topicType == CMQC.MQTOPT_LOCAL)//custom attribute is returned by MQ for local topics only
			   custom = pcfMessage.getStringParameterValue(CMQC.MQCA_CUSTOM);
		   
		   int defPersistence = pcfMessage.getIntParameterValue(CMQC.MQIA_TOPIC_DEF_PERSISTENCE);
		   String defPersistenceStr;
		   if(defPersistence == CMQC.MQPER_PERSISTENCE_AS_PARENT) 
				defPersistenceStr="MQPER_PERSISTENCE_AS_PARENT";
		   else if(defPersistence == CMQC.MQPER_PERSISTENT) 
				defPersistenceStr="MQPER_PERSISTENT";
		   else
				defPersistenceStr="MQPER_NOT_PERSISTENT";
		   
		   
		   int defPriority = pcfMessage.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY);
		   String defPriorityStr;
		   if(defPriority == CMQC.MQPRI_PRIORITY_AS_PARENT)
				defPriorityStr = "As parent";
		   else
				defPriorityStr = String.valueOf(defPriority);
		   
		   int defPutResponse = pcfMessage.getIntParameterValue(CMQC.MQIA_DEF_PUT_RESPONSE_TYPE);
		   String defPutResponseStr;
		   if(defPutResponse == CMQC.MQPRT_ASYNC_RESPONSE)
			   defPutResponseStr = "MQPRT_ASYNC_RESPONSE";
		   else if(defPutResponse == CMQC.MQPRT_RESPONSE_AS_PARENT)
			   defPutResponseStr = "MQPRT_RESPONSE_AS_PARENT";
		   else
			   defPutResponseStr = "MQPRT_SYNC_RESPONSE";
		   
		   
		   String durableModelQName = pcfMessage.getStringParameterValue(CMQC.MQCA_MODEL_DURABLE_Q);
		   
		   int durableSubscriptions = pcfMessage.getIntParameterValue(CMQC.MQIA_DURABLE_SUB);
		   String durableSubscriptionsStr;
		   if(durableSubscriptions == CMQC.MQSUB_DURABLE_AS_PARENT)
			   durableSubscriptionsStr = "MQSUB_DURABLE_AS_PARENT";
		   else if(durableSubscriptions == CMQC.MQSUB_DURABLE_NO)
			   durableSubscriptionsStr = "MQSUB_DURABLE_NO";
		   else
			   durableSubscriptionsStr ="MQSUB_DURABLE_YES";
		   
		   int inhibitPublications = pcfMessage.getIntParameterValue(CMQC.MQIA_INHIBIT_PUB);
		   String inhibitPublicationsStr;
		   if(inhibitPublications == CMQC.MQTA_PUB_AS_PARENT)
			   inhibitPublicationsStr = "MQTA_PUB_AS_PARENT";
		   else if(inhibitPublications == CMQC.MQTA_PUB_INHIBITED)
			   inhibitPublicationsStr = "MQTA_PUB_INHIBITED";
		   else
			   inhibitPublicationsStr = "MQTA_PUB_ALLOWED";
		   
		   int inhibitSubscriptions = pcfMessage.getIntParameterValue(CMQC.MQIA_INHIBIT_SUB);
		   String inhibitSubscriptionsStr;
		   if(inhibitSubscriptions == CMQC.MQTA_SUB_AS_PARENT)
			   inhibitSubscriptionsStr = "MQTA_SUB_AS_PARENT";
		   else if(inhibitSubscriptions == CMQC.MQTA_SUB_INHIBITED)
			   inhibitSubscriptionsStr = "MQTA_SUB_INHIBITED";
		   else
			   inhibitSubscriptionsStr = "MQTA_SUB_ALLOWED";	   
		   
		   String nonDurableModelQName = pcfMessage.getStringParameterValue(CMQC.MQCA_MODEL_NON_DURABLE_Q);
		   
		   int nonPersistentMsgDelivery = pcfMessage.getIntParameterValue(CMQC.MQIA_NPM_DELIVERY);
		   String nonPersistentMsgDeliveryStr;
		   if(nonPersistentMsgDelivery == CMQC.MQDLV_AS_PARENT)
			   nonPersistentMsgDeliveryStr = "MQDLV_AS_PARENT";
		   else if(nonPersistentMsgDelivery == CMQC.MQDLV_ALL)
			   nonPersistentMsgDeliveryStr = "MQDLV_ALL";
		   else if(nonPersistentMsgDelivery == CMQC.MQDLV_ALL_DUR)
			   nonPersistentMsgDeliveryStr = "MQDLV_ALL_DUR";
		   else
			   nonPersistentMsgDeliveryStr = "MQDLV_ALL_AVAIL";
		   
		   int persistentMsgDelivery = pcfMessage.getIntParameterValue(CMQC.MQIA_PM_DELIVERY);
		   String persistentMsgDeliveryStr;
		   if(persistentMsgDelivery == CMQC.MQDLV_AS_PARENT)
			   persistentMsgDeliveryStr = "MQDLV_AS_PARENT";
		   else if(persistentMsgDelivery == CMQC.MQDLV_ALL)
			   persistentMsgDeliveryStr = "MQDLV_ALL";
		   else if(persistentMsgDelivery == CMQC.MQDLV_ALL_DUR)
			   persistentMsgDeliveryStr = "MQDLV_ALL_DUR";
		   else
			   persistentMsgDeliveryStr = "MQDLV_ALL_AVAIL";	   
		   
		   int proxySubscriptions = pcfMessage.getIntParameterValue(CMQC.MQIA_PROXY_SUB);
		   String proxySubscriptionsStr;
		   if(proxySubscriptions == CMQC.MQTA_PROXY_SUB_FORCE)
			   proxySubscriptionsStr = "MQTA_PROXY_SUB_FORCE";
		   else
			   proxySubscriptionsStr = "MQTA_PROXY_SUB_FIRSTUSE";
		   
		   int publicationScope = pcfMessage.getIntParameterValue(CMQC.MQIA_PUB_SCOPE);
		   String publicationScopeStr;
		   if(publicationScope == CMQC.MQSCOPE_ALL)
			   publicationScopeStr = "MQSCOPE_ALL";
		   else if(publicationScope == CMQC.MQSCOPE_AS_PARENT)
			   publicationScopeStr = "MQSCOPE_AS_PARENT";
		   else
			   publicationScopeStr = "MQSCOPE_QMGR";
		   
		   
		   String qmgrName ="";
		   if(topicType == CMQC.MQTOPT_CLUSTER)//queue manager attribute is returned by MQ for cluster topics only
		    qmgrName = pcfMessage.getStringParameterValue(CMQC.MQCA_CLUSTER_Q_MGR_NAME);
		   
		   int subscriptionScope = pcfMessage.getIntParameterValue(CMQC.MQIA_SUB_SCOPE);
		   String subscriptionScopeStr;
		   if(subscriptionScope == CMQC.MQSCOPE_ALL)
			   subscriptionScopeStr = "MQSCOPE_ALL";
		   else if(subscriptionScope == CMQC.MQSCOPE_AS_PARENT)
			   subscriptionScopeStr = "MQSCOPE_AS_PARENT";
		   else
			   subscriptionScopeStr = "MQSCOPE_QMGR";	   
		   
		   String topicDesc = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_DESC);
		   String topicString = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim();
		   
		   int useDLQ = -1;
		   String useDLQStr = "N/A for cluster topic";

		   if(topicType == CMQC.MQTOPT_LOCAL)/*MQIA_USE_DEAD_LETTER_Q attribute is returned by MQ for local topics only*/{
			   useDLQ = pcfMessage.getIntParameterValue(CMQC.MQIA_USE_DEAD_LETTER_Q);
			   if(useDLQ == CMQC.MQUSEDLQ_NO)
				   useDLQStr = "MQUSEDLQ_NO";
			   else if(useDLQ == CMQC.MQUSEDLQ_YES)
				   useDLQStr = "MQUSEDLQ_YES";
			   else
				   useDLQStr = "MQUSEDLQ_AS_PARENT";	   
		   }
		   
		   
		   int wildcardOperation = pcfMessage.getIntParameterValue(CMQC.MQIA_WILDCARD_OPERATION);
		   String wildcardOperationStr;
		   if(wildcardOperation == CMQC.MQTA_PASSTHRU)
			   wildcardOperationStr = "MQTA_PASSTHRU";
		   else
			   wildcardOperationStr = "MQTA_BLOCK";	   
			
		   
			AdministrativeTopic administrativeTopic = new AdministrativeTopic(topicName, topicType,
					topicTypeStr, alterationDate, alterationTime,
					clusterName, custom, defPersistence,
					defPersistenceStr, defPriority, defPriorityStr,
					defPutResponse, defPutResponseStr,
					durableModelQName, durableSubscriptions,
					durableSubscriptionsStr, inhibitPublications,
					inhibitPublicationsStr, inhibitSubscriptions,
					inhibitSubscriptionsStr, nonDurableModelQName,
					nonPersistentMsgDelivery, nonPersistentMsgDeliveryStr,
					persistentMsgDelivery, persistentMsgDeliveryStr,
					proxySubscriptions, proxySubscriptionsStr,
					publicationScope, publicationScopeStr, qmgrName,
					subscriptionScope, subscriptionScopeStr,
					topicDesc, topicString, useDLQ, useDLQStr,
					wildcardOperation, wildcardOperationStr);
		   
		   return administrativeTopic;
	   }
	
	
}
