package com.wily.field.mqmonitoring.topicagent.dao;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.MQCFBS;
import com.ibm.mq.pcf.PCFMessage;

import java.io.IOException;

import com.wily.field.mqmonitoring.topicagent.entity.*;


/**
 * 
 * This class is responsible for creation of Subscription entity.
 * Purpose of this class is to make MQDAO more lightweight and 
 * include all logic of entity creation (Subscription) into separate class.
 * 
 * @author Adam Bezecny - CA Services
 *
 */
class SubscriptionCreator {

	protected static Subscription create(PCFMessage pcfMessage) throws IOException, MQException {
		
   		TopicString topicString = new TopicString(pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim());
   		

   		int subscriptionType = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUB_TYPE);
   		String subscriptionTypeStr;
   		
   		if(subscriptionType == CMQCFC.MQSUBTYPE_ADMIN)
   			subscriptionTypeStr = Subscription.SUBSCRIPTION_TYPE_STR_ADMIN;
   		else if(subscriptionType == CMQCFC.MQSUBTYPE_API)
   			subscriptionTypeStr = Subscription.SUBSCRIPTION_TYPE_STR_API;
   		else
   			subscriptionTypeStr = Subscription.SUBSCRIPTION_TYPE_STR_PROXY;
   		
   		byte[] subscriptionIDBytes = pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_SUB_ID);
   		String subscriptionId = MQCFBS.asHexString(subscriptionIDBytes).toUpperCase();
   		
   		String subscriptionUserId = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_SUB_USER_ID).trim();
   		int durable = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_DURABLE_SUBSCRIPTION);
   		
   		String resumeDate = pcfMessage.getStringParameterValue(CMQC.MQCA_RESUME_DATE);
   		String resumeTime = pcfMessage.getStringParameterValue(CMQC.MQCA_RESUME_TIME); 
   		String lastMessageDate = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_LAST_MSG_DATE);
   		String lastMessageTime = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_LAST_MSG_TIME); 
   		int numberOfMessages = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_MESSAGE_COUNT);
   		
   		byte[] activeConnectionBytes = pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_CONNECTION_ID);
   		String activeConnection = MQCFBS.asHexString(activeConnectionBytes).toUpperCase(); 	   		
   		

   		Subscription subscription = new Subscription(topicString, subscriptionId, subscriptionUserId,
   				durable, subscriptionType, subscriptionTypeStr, resumeDate,
   				resumeTime, lastMessageDate, lastMessageTime,
   				numberOfMessages, activeConnection);	
		
   		return subscription;
		
	}
	
}
