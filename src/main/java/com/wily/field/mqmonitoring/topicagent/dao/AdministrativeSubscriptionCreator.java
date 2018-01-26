package com.wily.field.mqmonitoring.topicagent.dao;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.MQCFBS;
import com.ibm.mq.pcf.PCFMessage;
import com.wily.field.mqmonitoring.topicagent.entity.AdministrativeSubscription;

/**
 * 
 * This class is responsible for creation of AdministrativeSubscription entity.
 * Purpose of this class is to make MQDAO more lightweight and 
 * include all logic of entity creation (AdministrativeSubscription) into separate class.
 * 
 * @author Adam Bezecny - CA Services
 *
 */
class AdministrativeSubscriptionCreator {

	protected static AdministrativeSubscription create(PCFMessage pcfMessage) throws IOException, MQException {
		
		byte[] subIdBytes = pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_SUB_ID);
		String subId = MQCFBS.asHexString(subIdBytes).toUpperCase();
		
		String subName = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_SUB_NAME);
		
		String alterationDate = pcfMessage.getStringParameterValue(CMQC.MQCA_ALTERATION_DATE);
		String alterationTime = pcfMessage.getStringParameterValue(CMQC.MQCA_ALTERATION_TIME);
		String creationDate = pcfMessage.getStringParameterValue(CMQC.MQCA_CREATION_DATE);
		String creationTime = pcfMessage.getStringParameterValue(CMQC.MQCA_CREATION_TIME);
		String destination = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_DESTINATION);
		
		int destinationClass =  pcfMessage.getIntParameterValue(CMQCFC.MQIACF_DESTINATION_CLASS);
		String destinationClassStr;
		if(destinationClass == CMQC.MQDC_MANAGED) 
			destinationClassStr = "MQDC_MANAGED";
		else
			destinationClassStr = "MQDC_PROVIDED";
		
		String destinationCorrelId = MQCFBS.asHexString(pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_DESTINATION_CORREL_ID)).toUpperCase();
		String destinationQueueManager = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_DESTINATION_Q_MGR);
		
		int expiry = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_EXPIRY);
		String expiryStr;
		if(expiry == -1)
			expiryStr ="UNLIMITED";
		else
			expiryStr = String.valueOf(expiry);
		
		String publishedAccountingToken = MQCFBS.asHexString(pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_ACCOUNTING_TOKEN)).toUpperCase();
		String publishedApplicationIdentityData = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_APPL_IDENTITY_DATA);
		
		int publishPriority = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_DESTINATION_CLASS);
		String publishPriorityStr;
		if(publishPriority == CMQC.MQPRI_PRIORITY_AS_PUBLISHED)
			publishPriorityStr = "MQPRI_PRIORITY_AS_PUBLISHED";
		else if(publishPriority == CMQC.MQPRI_PRIORITY_AS_Q_DEF)
			publishPriorityStr = "MQPRI_PRIORITY_AS_Q_DEF";
		else
			publishPriorityStr = String.valueOf(publishPriority);
		
		
		int publishSubscribeProperties = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_PUBSUB_PROPERTIES);
		String publishSubscribePropertiesStr;
		if(publishSubscribeProperties == CMQC.MQPSPROP_NONE)
			publishSubscribePropertiesStr = "MQPSPROP_NONE";
		else if(publishSubscribeProperties == CMQC.MQPSPROP_MSGPROP)
			publishSubscribePropertiesStr = "MQPSPROP_MSGPROP";
		else if(publishSubscribeProperties == CMQC.MQPSPROP_COMPAT)
			publishSubscribePropertiesStr = "MQPSPROP_COMPAT";
		else
			publishSubscribePropertiesStr ="MQPSPROP_RFH2";
		
		int requestonly = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_REQUEST_ONLY);
		String requestonlyStr;
		if(requestonly == CMQC.MQRU_PUBLISH_ALL)
			requestonlyStr ="MQRU_PUBLISH_ALL";
		else
			requestonlyStr = "MQRU_PUBLISH_ON_REQUEST";
		
		String selector = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_SUB_SELECTOR);
		
		int selectorType = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SELECTOR_TYPE);
		String selectorTypeStr;
		if(selectorType == CMQCFC.MQSELTYPE_NONE)
			selectorTypeStr = "MQSELTYPE_NONE";
		else if(selectorType == CMQCFC.MQSELTYPE_STANDARD)
			selectorTypeStr = "MQSELTYPE_STANDARD";
		else
			selectorTypeStr = "MQSELTYPE_EXTENDED";
		
		int subscriptionLevel = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUB_LEVEL);
		
		int subscriptionScope = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUBSCRIPTION_SCOPE);
		String subscriptionScopeStr;
		if(subscriptionScope ==CMQC.MQTSCOPE_ALL)
			subscriptionScopeStr = "MQTSCOPE_ALL";
		else
			subscriptionScopeStr = "MQTSCOPE_QMGR";
		
		
		int subscriptionType = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUB_TYPE);
		String subscriptionTypeStr;
		if(subscriptionType ==CMQCFC.MQSUBTYPE_PROXY)
			subscriptionTypeStr = "MQSUBTYPE_PROXY";
		if(subscriptionType ==CMQCFC.MQSUBTYPE_ADMIN)
			subscriptionTypeStr = "MQSUBTYPE_ADMIN";
		else
			subscriptionTypeStr = "MQSUBTYPE_API";
		
		String subscriptionUser = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_SUB_USER_ID); 
		String topicObject = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
		String topicString = pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING);
		String userdata = pcfMessage.getStringParameterValue(CMQCFC.MQCACF_SUB_USER_DATA);
		
		
		int variableUser = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_VARIABLE_USER_ID);
		String variableUserStr;
		if(variableUser == CMQC.MQVU_ANY_USER)
			variableUserStr = "MQVU_ANY_USER";
		else
			variableUserStr = "MQVU_FIXED_USER";
		
		int wildcardSchema = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_WILDCARD_SCHEMA);
		String wildcardSchemaStr;	
		if(wildcardSchema == CMQC.MQWS_CHAR)
			wildcardSchemaStr = "MQWS_CHAR";
		else
			wildcardSchemaStr = "MQWS_TOPIC";
		
		return new AdministrativeSubscription(subId, subIdBytes, subName,
				alterationDate, alterationTime, creationDate,
				creationTime, destination, destinationClass,
				destinationClassStr, destinationCorrelId,
				destinationQueueManager, expiry, expiryStr,
				publishedAccountingToken,
				publishedApplicationIdentityData, publishPriority,
				publishPriorityStr, publishSubscribeProperties,
				publishSubscribePropertiesStr, requestonly,
				requestonlyStr, selector, selectorType,
				selectorTypeStr, subscriptionLevel,
				subscriptionScope, subscriptionScopeStr,
				subscriptionType, subscriptionTypeStr,
				subscriptionUser, topicObject, topicString,
				userdata, variableUser, variableUserStr,
				wildcardSchema, wildcardSchemaStr);
	}
	
}
