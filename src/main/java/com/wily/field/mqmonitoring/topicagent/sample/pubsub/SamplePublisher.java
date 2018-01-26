package com.wily.field.mqmonitoring.topicagent.sample.pubsub;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.MQPutMessageOptions; 
import com.wily.field.mqmonitoring.topicagent.dao.MQDAO;
import com.wily.field.mqmonitoring.topicagent.dao.MQProperties;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterDummy;
import com.ibm.mq.MQTopic;
import com.wily.field.mqmonitoring.topicagent.cli.CLIFactory;

/**
 * 
 * This is sample publisher that was used to test TopicAgent
 * 
 * @author Adam Bezecny - CA Services
 *
 */
public class SamplePublisher {

	private static MetricWriter metricWriter = null;
	private static MQProperties mqProperties = null;
	private static MQDAO mqDao = null;
	
	private String qmHost;
	private int qmPort;
	private String qmChannel;
	private String qmName;
	private String qmUser;
	private String qmPassword;
	
	private String messagePrefix;
	private int messageCount;
	private int messageDelay;
	
	private String topicString;
	
	private MQTopic publisher;
	
	public SamplePublisher(CommandLine cmdLine) throws Exception {

		this.qmHost		= cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_HOST, CLIFactory.CLI_OPT_QM_HOST_DEFAULT);
		this.qmPort		= Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PORT, CLIFactory.CLI_OPT_QM_PORT_DEFAULT));
		this.qmChannel	= cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_CHNL, CLIFactory.CLI_OPT_QM_CHNL_DEFAULT);
		this.qmName		= cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_NAME);
		this.qmUser		= cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_USER);
		this.qmPassword	= cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PWD);
		

		this.messagePrefix = cmdLine.getOptionValue(CLIFactory.CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_PREFIX);
		this.messageCount  = Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_COUNT));
		this.messageDelay  = Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_DELAY));
		this.topicString   = cmdLine.getOptionValue(CLIFactory.CLI_OPT_SAMPLE_PUBLISHER_TOPIC_STRING);
		
    	mqProperties = new MQProperties(this.qmHost, this.qmPort, this.qmChannel, this.qmName, this.qmUser, this.qmPassword);
    	
    	metricWriter = new MetricWriterDummy(this.qmName, 15);
    	
    	mqDao = new MQDAO(mqProperties, metricWriter);
    	
    	publisher = mqDao.getMQQueueManager().accessTopic(this.topicString, "", CMQC.MQTOPIC_OPEN_AS_PUBLICATION, CMQC.MQOO_OUTPUT);
    	
	}
	
	
	/**
	 * Just sample code if we ever needed to publish using topic name instead of topic string
	 * @param message
	 * @throws MQException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void publishToTopicName(String message) throws MQException, IOException {
		    
		  	int destinationType = CMQC.MQOT_TOPIC;
		  	
		    MQMessage messageForPut = new MQMessage();
		    System.out.println("doPublish(): Publishing message " + message);
		    messageForPut.writeString(message);
		    //MQJE001: Completion Code '2', Reason '2085'. = Unknown Object
		    //This will be thrown if unknown topic name will be specified
		    ////Price.Fruit is topic name (i.e. name of administratively defined topic object), not topic string!
		    mqDao.getMQQueueManager().put(destinationType, "Price.Fruit", messageForPut);
		  
	  }
	  
	  private void publishToTopicString(String message) throws MQException, IOException {
		  //MQTopic publisher = mqDao.getMQQueueManager().accessTopic(this.topicString, "", CMQC.MQTOPIC_OPEN_AS_PUBLICATION, CMQC.MQOO_OUTPUT);
		  MQPutMessageOptions msgOpts = new MQPutMessageOptions();
		  
		  MQMessage mqMessage = new MQMessage(); 
		  mqMessage.writeString(message); 		  
		  publisher.put(mqMessage, msgOpts);
		  
	  }
	
	  private void publish() throws MQException, IOException, InterruptedException{
		 
		  for(int i=0; i < this.messageCount;i++){
			  String msg = this.messagePrefix + " " + i;
			  System.out.println("Publishing ["+this.topicString+"] >>> " + msg);
			  publishToTopicString(msg);
			  Thread.sleep(this.messageDelay);
		  }
		  
		  mqDao.disconnect();
	  }

	  public static void main(String[] args) throws Exception{
		  
		  //Sample command line:
		  //-qmhost "localhost" -qmport 1415 -qmchannel "SYSTEM.DEF.SVRCONN" -qmname "MQDEVQM02" -qmuser "bezad01@TANT-A01" -qmpwd "<<MyPwd>>" -msgprefix "Hello Adam" -msgcount 3 -msgdelay 5 -topicStr "Price/Fruit"
		  
		  System.out.println("Starting sample MQ publisher...");
		  
		  Options options = CLIFactory.getSamplePublisherCLIDefinition();
		  CommandLineParser parser = new DefaultParser();
		  CommandLine cmdLine = parser.parse(options, args);
		  
		  SamplePublisher publisher = new SamplePublisher(cmdLine);
		  publisher.publish();
		  
		  System.out.println("All messages have been published!");
		  
	  }
}