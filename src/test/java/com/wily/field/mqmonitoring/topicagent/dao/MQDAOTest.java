package com.wily.field.mqmonitoring.topicagent.dao;

import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.PCFMessage;
import com.wily.field.mqmonitoring.topicagent.dao.MQDAO;
import com.wily.field.mqmonitoring.topicagent.entity.*;
import com.wily.field.mqmonitoring.topicagent.metricwriter.*;

/**
 * 
 * @author Adam Bezecny - CA Services
 * Test relying on specific environment (local DEV environment used to develop this extension)
 * are -after being executed on this specific environment- marked with @Ignore so that they are not executed any more during builds, etc. 
 *
 */
public class MQDAOTest {

	private static MetricWriter metricWriter = null;
	
	@SuppressWarnings("unused")
	private static MQProperties mqProperties = null;
	
	private static MQDAO mqDao = null;
	
    @BeforeClass
    public static void setUp() throws Exception{
    	System.out.println("MQDAOTest.setUp()");
    	
    	//metricWriter = new MetricWriterDummy("MQDEVQM01", 15);
    	
    	//metricWriter = new MetricWriterREST("MQDEVQM02", "http://localhost:9777/apm/metricFeed"); 
    	
    	//
    	//local queue managers
    	//
    	//mqProperties = new MQProperties("localhost", 1414,"SYSTEM.DEF.SVRCONN", "MQDEVQM01", com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_PASSWORD);
    	//mqProperties = new MQProperties("localhost", 1415,"SYSTEM.DEF.SVRCONN", "MQDEVQM02", com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_PASSWORD);
    	//mqProperties = new MQProperties("localhost", 1416,"SYSTEM.DEF.SVRCONN", "MQDEVQM03", com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.LOCAL_MQ_PASSWORD);
    	
    	//
    	//CA cloud queue managers
    	//
    	//mqProperties = new MQProperties(com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_HOST, 1416,"SYSTEM.DEF.SVRCONN", "ABEQM01", com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_PASSWORD);
    	//mqProperties = new MQProperties(com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_HOST, 1417,"SYSTEM.DEF.SVRCONN", "ABEQM02", com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_PASSWORD);
    	//mqProperties = new MQProperties(com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_HOST, 1418,"SYSTEM.DEF.SVRCONN", "ABEQM03", com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_USER, com.wily.field.mqmonitoring.topicagent.Constants.CA_MQ_PASSWORD);

    	//mqDao = new MQDAO(mqProperties, metricWriter);
    }
    
    @AfterClass
    public static void tearDown() {
    	System.out.println("MQDAOTest.tearDown()");
    	//mqDao.disconnect();   
    }
	
    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
    
    @Ignore
    @Test
    public void getTopicNamesTest() throws MQException, IOException{
    	
    	String[] topicNames= mqDao.getTopicNames();
    	System.out.println("-------------------------------------");
    	for(int i=0;i<topicNames.length;i++)
    		System.out.println(topicNames[i]);
    	System.out.println("-------------------------------------");
    }
    
  
    @Ignore
    @Test     
    public void getTopicsTest() throws MQException, IOException{
    	
    	Topics topics = mqDao.getTopics();
    	
    	System.out.println("-------------------------------------");
    	for(Map.Entry<TopicString, Topic> entry : topics.getTopics().entrySet()){
    		System.out.println(entry.getValue().toString());
    	}
    	System.out.println("-------------------------------------");
    }

    
    
    @Ignore
    @Test     
    public void getAdministrativeTopicTest() throws MQException, IOException{
    
    	AdministrativeTopic atopic = mqDao.getAdministrativeTopic("Price.Fruit");
    	//AdministrativeTopic atopic = mqDao.getAdministrativeTopic("News.Topic");
    	System.out.println(atopic.toString());
    }

    @Ignore
    @Test     
    public void pcfInquireTopicStatusSubTest() throws MQException, IOException{
    	
    	PCFMessage[] pcfMessages = mqDao.pcfInquireTopicStatusSub();
    	
    	System.out.println("-------------------------------------");
    	for(PCFMessage pcfMessage : pcfMessages){
    		
	   		TopicString topicString = new TopicString(pcfMessage.getStringParameterValue(CMQC.MQCA_TOPIC_STRING).trim());
	   		

	   		//filter out system subscriptions
	   		if(topicString.getTopicString().startsWith("SYSTEM.")) continue;
	   		
	   		int subscriptionType = pcfMessage.getIntParameterValue(CMQCFC.MQIACF_SUB_TYPE);
	   		
	   		switch(subscriptionType){
	   			case CMQCFC.MQSUBTYPE_ADMIN:
	   				break;
	   			case CMQCFC.MQSUBTYPE_API:
	   				break;
	   			case CMQCFC.MQSUBTYPE_PROXY:
	   				continue;//filter out PROXY subscriptions
	   		}

	   		Subscription subscription = SubscriptionCreator.create(pcfMessage);
    		
	   		System.out.println(subscription.toString());
    		
    	}
    	System.out.println("-------------------------------------");
    	
    }
    
    @Ignore
    @Test     
    public void getAdministrativeSubscriptionTest() throws MQException, IOException{
    
    	//subId = 414D51204D51444556514D30312020208BD80958200040F7, subName = SMSSubscription
    	byte[] subId = {65, 77, 81, 32, 77, 81, 68, 69, 86, 81, 77, 48, 49, 32, 32, 32, -117, -40, 9, 88, 32, 0, 64, -9};
    	
    	AdministrativeSubscription asubscription = mqDao.getAdministrativeSubscription(subId);
    	
    	System.out.println("-------------------------------------");
    	System.out.println(asubscription.toString());
    	System.out.println("-------------------------------------");
    }

    
    /**
     * This test requires REST metric writer to be configured in setUp() method
     * @throws MQException
     * @throws IOException
     */
    @Ignore
    @Test     
    public void renderTopicSpace() throws MQException, IOException{

    	Topics topics = mqDao.retrieveTopicSpace();
    	metricWriter.generateTopicMetrics(topics, null);
    	System.out.println("renderTopicSpace() done!");
    	
    }    
    
    
}
