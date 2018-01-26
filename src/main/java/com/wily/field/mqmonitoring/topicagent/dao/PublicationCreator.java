package com.wily.field.mqmonitoring.topicagent.dao;

import java.io.IOException;
import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.MQCFBS;
import com.ibm.mq.pcf.PCFMessage;
import com.wily.field.mqmonitoring.topicagent.entity.Publication;
import com.wily.field.mqmonitoring.topicagent.entity.TopicString;

/**
 * 
 * This class is responsible for creation of Publication entity.
 * Purpose of this class is to make MQDAO more lightweight and 
 * include all logic of entity creation (Publication) into separate class.
 * 
 * @author Adam Bezecny - CA Services
 *
 */
class PublicationCreator {

	protected static Publication create(PCFMessage pcfMessage) throws IOException, MQException {
		
		   TopicString topicString = new TopicString(pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim());
		
	   		String lastPublishDate  =  pcfMessage.getStringParameterValue(CMQCFC.MQCACF_LAST_PUB_DATE).trim();
	   		String lastPublishTime  =  pcfMessage.getStringParameterValue(CMQCFC.MQCACF_LAST_PUB_TIME).trim();
	   		int numberOfPublishes   =  pcfMessage.getIntParameterValue(CMQCFC.MQIACF_PUBLISH_COUNT);
	   		String activeConnection =  MQCFBS.asHexString(pcfMessage.getBytesParameterValue(CMQCFC.MQBACF_CONNECTION_ID)).toUpperCase();

	   		return new Publication(topicString, lastPublishDate, lastPublishTime, numberOfPublishes, activeConnection);
	   		
		
	}
	
}
