package com.wily.field.mqmonitoring.topicagent.sample.pubsub;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.ibm.mq.jms.MQTopic;
import com.ibm.mq.jms.MQTopicConnection;
import com.ibm.mq.jms.MQTopicConnectionFactory;
import com.ibm.mq.jms.MQTopicSession;
import com.ibm.msg.client.wmq.WMQConstants;
import com.wily.field.mqmonitoring.topicagent.cli.CLIFactory;

/**
 * 
 * This is sample subscriber that was used to test TopicAgent. Apart from
 * SamplePublisher which utilizes WMQ Java API only SampleSubscriber is relying
 * on JMS API because it seems WMQ Java API for IBM MQ 7.5 does not support 
 * asynchronous (via callbacks) consumption of messages (at least I did not find anything on google:) )
 * 
 * @author Adam Bezecny - CA Services
 *
 */
public class SampleSubscriber {

    private MQTopicConnection connection = null;
    private MQTopicSession session = null;
    private MQTopic topic = null;
	private String topicString;
	private MQTopicConnectionFactory cf;
	
	private String qmUser;
	private String qmPassword;
	
	
	public SampleSubscriber(CommandLine cmdLine) throws NumberFormatException, JMSException {
		
		  this.cf = new MQTopicConnectionFactory();

		  this.cf.setHostName(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_HOST, CLIFactory.CLI_OPT_QM_HOST_DEFAULT));
		  this.cf.setPort(Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PORT, CLIFactory.CLI_OPT_QM_PORT_DEFAULT)));
		  this.cf.setQueueManager(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_NAME));
		  this.cf.setChannel(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_CHNL, CLIFactory.CLI_OPT_QM_CHNL_DEFAULT));
		  
		  this.qmUser	  = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_USER);
		  this.qmPassword = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PWD);
		  
		  this.cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
		  
	      this.topicString = cmdLine.getOptionValue(CLIFactory.CLI_OPT_SAMPLE_SUBSCRIBER_TOPIC_STRING);
	      
	}
	
	private void subscribe() throws JMSException, InterruptedException{

		  connection = (MQTopicConnection) this.cf.createTopicConnection(this.qmUser, this.qmPassword);
	      session = (MQTopicSession) connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	      topic = (MQTopic) session.createTopic(topicString);
	      
	      MessageConsumer  consumer = session.createConsumer(topic);
	      
	      
	      consumer.setMessageListener(new MessageListener(){
	    	  public void onMessage(Message message){
	    		  
	    		  
	    		  if(message instanceof TextMessage){
	    			  TextMessage tmsg = (TextMessage)message;
	    			  try {
						System.out.println("Received message: "+tmsg.getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
	    		  }else{
	    			  System.out.println("Received message -> "+message.toString());  
	    		  }
	    		  
	    		  
	    	  }
	      });
	      
	      System.out.println("Subscribing now (Topic String = "+this.topicString+")...");
	      connection.start();
	      while(true){
	    	  Thread.sleep(1000);
	      }
		
		
	}
	
	public static void main(String[] args) throws JMSException, InterruptedException, ParseException {
		
		  //Sample command line:
		  //-qmhost "localhost" -qmport 1415 -qmchannel "SYSTEM.DEF.SVRCONN" -qmname "MQDEVQM02" -topicStr "Price/Fruit" -qmuser "bezad01@TANT-A01" -qmpwd "<<MyPwd>>"
		
		  System.out.println("Starting sample MQ subscriber...");
		
		  Options options = CLIFactory.getSampleSubscriberCLIDefinition();
		  CommandLineParser parser = new DefaultParser();
		  CommandLine cmdLine = parser.parse(options, args);
		
		  SampleSubscriber subscriber = new SampleSubscriber(cmdLine);
		  subscriber.subscribe();
		
	}
	
}
