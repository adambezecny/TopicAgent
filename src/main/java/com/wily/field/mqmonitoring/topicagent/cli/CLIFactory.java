package com.wily.field.mqmonitoring.topicagent.cli;

import org.apache.commons.cli.*;

/**
 * @author Adam Bezecny -  CA Services
 * This class holds definitions for Command Line Interface of both main application
 * and also sample publisher and subscriber applications
 * 
 */
public class CLIFactory {

	public static final String CLI_OPT_QM_HOST 							= "qmhost";
	public static final String CLI_OPT_QM_HOST_DEFAULT 					= "localhost";
	
	public static final String CLI_OPT_QM_PORT 							= "qmport";
	public static final String CLI_OPT_QM_PORT_DEFAULT 					= "1414";
	
	public static final String CLI_OPT_QM_CHNL 							= "qmchannel";
	public static final String CLI_OPT_QM_CHNL_DEFAULT 					= "SYSTEM.DEF.SVRCONN";
	
	public static final String CLI_OPT_QM_NAME 							= "qmname";
	public static final String CLI_OPT_QM_USER 							= "qmuser";
	public static final String CLI_OPT_QM_PWD  							= "qmpwd";

	public static final String CLI_OPT_QM_POLLING_INTERVAL  			= "pollingIntervalInSec";
	public static final String CLI_OPT_QM_POLLING_INTERVAL_DEFAULT  	= "60";
	
	
	public static final String CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_PREFIX  = "msgprefix";
	public static final String CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_COUNT  	= "msgcount";
	public static final String CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_DELAY 	= "msgdelay";
	public static final String CLI_OPT_SAMPLE_PUBLISHER_TOPIC_STRING  	= "topicStr";
	
	public static final String CLI_OPT_SAMPLE_SUBSCRIBER_TOPIC_STRING  	= "topicStr";

	public static final String CLI_OPT_EPA_REST_URL					  	= "epaRESTUrl";
	
	
	
	/**
	 * common MQ connection parameters except user & password which is not used
	 * by {@link com.wily.field.mqmonitoring.topicagent.sample.pubsub.SampleSubscriber}
	 * 
	 * @return
	 */
	private static Options getMQConnectivityCLIOptions(){
		
		Options options = new Options();
		
		Option qmHostOption = Option.builder("h")
                .longOpt(CLI_OPT_QM_HOST)
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("Host anme or IP of monitored queue manager.")
                .build();

		Option qmPortOption = Option.builder("p")
                .longOpt(CLI_OPT_QM_PORT)
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Port of monitored queue manager.")
                .build();

		Option qmChannelOption = Option.builder("ch")
                .longOpt(CLI_OPT_QM_CHNL)
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("Channel used to connect to queue manager.")
                .build();

		Option qmNameOption = Option.builder("n")
                .longOpt(CLI_OPT_QM_NAME)
                .numberOfArgs(1)
                .required(true)
                .type(String.class)
                .desc("Queue manager name.")
                .build();
		
		Option qmUserOption = Option.builder("u")
                .longOpt(CLI_OPT_QM_USER)
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("Username used to connect to queue manager.")
                .build();

		Option qmPasswordOption = Option.builder("pwd")
                .longOpt(CLI_OPT_QM_PWD)
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("Password used to connect to queue manager.")
                .build();
		

		
		options.addOption(qmHostOption);
		options.addOption(qmPortOption);
		options.addOption(qmChannelOption);
		options.addOption(qmNameOption);
		options.addOption(qmUserOption);
		options.addOption(qmPasswordOption);
		
		return options;
		
	}
	
	/**
	 * Returns CLI syntax definition for main TopicAgent component {@link com.wily.field.mqmonitoring.topicagent.TopicAgent}
	 * @return
	 */
	public static Options getTopicAgentCLIDefinition(){
		
		Options options = getMQConnectivityCLIOptions();
		
		Option pollingIntervalfixOption = Option.builder("pi")
                .longOpt(CLI_OPT_QM_POLLING_INTERVAL)
                .numberOfArgs(1)
                .required(false)
                .type(Integer.class)
                .desc("Polling interval (in seconds) specifying how often will TopicAgent poll MQ manager. Default is 60 seconds.")
                .build();

		Option epaRESTUrlOption = Option.builder("url")
                .longOpt(CLI_OPT_EPA_REST_URL)
                .numberOfArgs(1)
                .required(false)
                .type(String.class)
                .desc("URL where EPAgent accepts metric data via HTTP POST requests with JSON payload. ")
                .build();
		
		
		
		options.addOption(pollingIntervalfixOption);
		options.addOption(epaRESTUrlOption);
		
		return options;
	}	
	
	
	/**
	 * Returns CLI syntax definition for SamplePublisher component {@link com.wily.field.mqmonitoring.topicagent.sample.pubsub.SampleSubscriber}
	 * @return
	 */
	public static Options getSampleSubscriberCLIDefinition(){
		
		Options options = getMQConnectivityCLIOptions();
		
		Option topicStringOption = Option.builder("t")
                .longOpt(CLI_OPT_SAMPLE_SUBSCRIBER_TOPIC_STRING)
                .numberOfArgs(1)
                .required(true)
                .type(String.class)
                .desc("Topic string where subscriber will listen for messages.")
                .build();
		
		options.addOption(topicStringOption);
		
		return options;
	}
	
	/**
	 * Returns CLI syntax definition for SamplePublisher component {@link com.wily.field.mqmonitoring.topicagent.sample.pubsub.SamplePublisher}
	 * @return
	 */
	public static Options getSamplePublisherCLIDefinition(){
		
		Options options = getMQConnectivityCLIOptions();

		Option messagePrefixOption = Option.builder("pfx")
                .longOpt(CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_PREFIX)
                .numberOfArgs(1)
                .required(true)
                .type(String.class)
                .desc("Common prefix of all messages published by sample publisher. Sequence number of published message is added to this prefix.")
                .build();

		Option messageCountOption = Option.builder("c")
                .longOpt(CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_COUNT)
                .numberOfArgs(1)
                .required(true)
                .type(Integer.class)
                .desc("Number of messages to publish by sample publisher.")
                .build();

		Option messageDelayOption = Option.builder("d")
                .longOpt(CLI_OPT_SAMPLE_PUBLISHER_MESSAGE_DELAY)
                .numberOfArgs(1)
                .required(true)
                .type(Integer.class)
                .desc("Delay in miliseconds between publishing two messages by sample publisher.")
                .build();
		
		Option topicStringOption = Option.builder("t")
                .longOpt(CLI_OPT_SAMPLE_PUBLISHER_TOPIC_STRING)
                .numberOfArgs(1)
                .required(true)
                .type(String.class)
                .desc("Topic string used by publisher.")
                .build();
		
		
		options.addOption(messagePrefixOption);
		options.addOption(messageCountOption);
		options.addOption(messageDelayOption);
		options.addOption(topicStringOption);
		
		return options;
		
	}
	
	
	public static void printHelp(){
		
		String header = "CA APM Topic Agent\n\n";
		String footer = "\n(C) CA Technologies, 2016";
		
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp(200, "java -jar topicagent-0.0.1-SNAPSHOT.jar", header, getTopicAgentCLIDefinition(), footer, true);
		
	}	
	
}
