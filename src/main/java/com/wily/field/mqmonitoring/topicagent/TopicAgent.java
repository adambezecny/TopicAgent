package com.wily.field.mqmonitoring.topicagent;

import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.constants.CMQC;
import com.wily.field.mqmonitoring.topicagent.cli.CLIFactory;
import com.wily.field.mqmonitoring.topicagent.cli.CLIValidator;
import com.wily.field.mqmonitoring.topicagent.dao.MQDAO;
import com.wily.field.mqmonitoring.topicagent.dao.MQProperties;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterISC;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterREST;
import com.wily.field.mqmonitoring.topicagent.metricwriter.SupportabilityMetrics;
import com.wily.field.mqmonitoring.topicagent.entity.Topics;

import static com.wily.introscope.epagent.EPALogger.*;

/**
 * Topic Agent main entry point
 * @author Adam Bezecny - CA Services
 *
 */
public class TopicAgent {
	
	
	private MetricWriter metricWriter = null;
	private MQProperties mqProperties = null;
	private MQDAO mqDao = null;
	
	private String qmHost;
	private int qmPort;
	private String qmChannel;
	private String qmName;
	private String qmUser;
	private String qmPassword;
	private String epaURL;
	
	private int pollingInterval;
	
	private Topics topicsCurrentCycle;
	private Topics topicsPreviousCycle;
	private MQPoolToken mqPoolToken;
	private boolean firstRun;
	private boolean tryToReconnect;
	
	
	/**
	 * 
	 * @param cmdLine
	 * @param printStream
	 * @throws Exception
	 */
	public TopicAgent(CommandLine cmdLine, PrintStream printStream) throws Exception {
		
		CLIValidator.validatePollingInterval(cmdLine);
		
		this.qmHost			 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_HOST, CLIFactory.CLI_OPT_QM_HOST_DEFAULT);
		this.qmPort			 = Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PORT, CLIFactory.CLI_OPT_QM_PORT_DEFAULT));
		this.qmChannel		 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_CHNL, CLIFactory.CLI_OPT_QM_CHNL_DEFAULT);
		this.qmName			 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_NAME);
		this.qmUser			 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_USER, null);
		this.qmPassword		 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_PWD, null);
		this.epaURL 		 = cmdLine.getOptionValue(CLIFactory.CLI_OPT_EPA_REST_URL, "N/A");
		this.pollingInterval = Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_POLLING_INTERVAL, CLIFactory.CLI_OPT_QM_POLLING_INTERVAL_DEFAULT));
		
		if(printStream != null)
			metricWriter = new MetricWriterISC(printStream, this.qmName, this.pollingInterval);
		else
			metricWriter = new MetricWriterREST(this.qmName, this.epaURL, this.pollingInterval); 
	
		
		this.mqProperties = new MQProperties(this.qmHost, this.qmPort,this.qmChannel, this.qmName, this.qmUser, this.qmPassword);
		
		connectMQ();
		
		this.firstRun = true;
		this.tryToReconnect = false;
		
		epainfo("Using following parameters:");
		epainfo("qmHost: " + this.qmHost);
		epainfo("qmPort: " + this.qmPort);
		epainfo("qmChannel: " + this.qmChannel);
		epainfo("qmName: " + this.qmName);
		epainfo("qmUser: " + this.qmUser);
		if(!this.epaURL.equals("N/A")) epainfo("epaURL: " + this.epaURL);
		epainfo("pollingInterval: " + this.pollingInterval);
		
		this.metricWriter.forceSupportabilityMetrics();
		
	}
	
	/**
	 * main loop (infinite) where monitoring data are 
	 * fetched and rendered into metrics infinitely 
	 */
	public void monitoringLoop(){
		
		try{
			
			while(true)/* MAIN PROCESSING LOOP */{
				
				Thread dataFetcherThread = new Thread(new Runnable() {
		            public void run() {
		                try {
		                	
		                	if(!firstRun)
		                		topicsPreviousCycle = topicsCurrentCycle;
		                	else
		                		firstRun = false;
		                	
		                	if(tryToReconnect){
		                		epainfo("Trying to reconnect to queue manager " + qmName);
		                		connectMQ();//if this fail again we get MQRC_HOST_NOT_AVAILABLE which is processed below
		                		tryToReconnect = false;
		                		epainfo("Reconnect attempt to queue manager " + qmName + " was successful.");
		                		topicsPreviousCycle = null;//after reconnect let's better empty previous cycle 
		                									//to prevent negative delta metrics being calculated(I saw it once during testing)
		                	}

		                	metricWriter.writeQueuemanagerConnectionStatusMetric(MetricWriter.QUEUEMANAGER_CONNECTION_OK);
		                	
		                	topicsCurrentCycle = mqDao.retrieveTopicSpace();
		                	
		                } catch (MQException mqe) {	
		                	int completionCode = mqe.completionCode;
		                	int reasonCode = mqe.reasonCode;
		                	
		                	epaerror("MQException for queue manager " + qmName + " completionCode = " + completionCode+ ", reasonCode = " + reasonCode);
		                	
		                	if(completionCode == 2 && 
		                	   (  reasonCode == CMQC.MQRC_CONNECTION_BROKEN ||
		                		  reasonCode == CMQC.MQRC_CONNECTION_ERROR ||
		                		  reasonCode == CMQC.MQRC_CONNECTION_NOT_AUTHORIZED ||
		                		  reasonCode == CMQC.MQRC_HOST_NOT_AVAILABLE) 
		                	){
		                		epaerror("Connection with queue manager " + qmName + " lost! " + mqe.getMessage());

		                		//after MQ connection error let's report MQ connectivity as KO
		                		metricWriter.writeQueuemanagerConnectionStatusMetric(MetricWriter.QUEUEMANAGER_CONNECTION_KO);
		                		tryToReconnect = true;
		                	}
		                	
						} catch (Exception e) {
							epaerror("Error while fetching topic space " + e.getMessage());
						} 
		            }
		        });
				
				
				long startTime = System.currentTimeMillis();
				long durationFetch = 0;
				long durationWrite = 0;
				
				dataFetcherThread.start();
				
				try {
					//use at most 80 per cent of pollingInterval for data fetching
					//remaining time is reserved for metric writing and overall processing
					long joinTime = (long)(this.pollingInterval * 1000 * 0.8);
					dataFetcherThread.join(joinTime);
					//dataFetcherThread.join();//this call waits forever. use it for debugging only!
					
					durationFetch = System.currentTimeMillis() - startTime;
					
					startTime = System.currentTimeMillis();
					this.metricWriter.writeSupportabilityMetric(SupportabilityMetrics.MQ_DATA_FETCH_TIME, this.qmName, durationFetch);
					
					this.metricWriter.generateTopicMetrics(topicsCurrentCycle, topicsPreviousCycle);
					durationWrite = System.currentTimeMillis() - startTime;

					this.metricWriter.writeSupportabilityMetric(SupportabilityMetrics.MQ_DATA_METRIC_WRITE_TIME, this.qmName, durationWrite);
				
				   
				    long sleepTime = (this.pollingInterval * 1000 - (durationFetch + durationWrite));
				    if(sleepTime > 0)
				    	Thread.sleep(sleepTime);//use remaining time for sleeping
				    	
				} catch (InterruptedException ie) {
		            epaerror("InterruptedException in main monitoring loop of TopicAgent " + ie.getMessage());
		            ie.printStackTrace();
				}
				
			}
			
		}finally{
			disconnectMQ();
		}
		
	}
	
	/**
	 * Connects to MQ
	 * @throws MQException
	 */
	private void connectMQ() throws MQException {
		this.mqPoolToken = MQEnvironment.addConnectionPoolToken();
		this.mqDao = new MQDAO(mqProperties, metricWriter);
	}
	
	/**
	 * Disconnects from MQ
	 */
	private void disconnectMQ(){
		
		try{

			epainfo("Disconnecting from MQ...");
			this.mqDao.disconnect();
			MQEnvironment.removeConnectionPoolToken(this.mqPoolToken);
			epainfo("Disconnected from MQ!");
			
		}catch(Exception e){
			epaerror("Error in TopicAgent.disconnectMQ(): " + e.getMessage());
		}
		
	}
	
	/**
	 * 
	 * Common method for initializing EPAgent plug-in. Initialization is synchronized
	 * in order to better support multiple plug-in configuration per single EPAGent instance as 
	 * outlined below:
	 * 
	 * 
	 * introscope.epagent.plugins.stateful.names=topicMonitor1,topicMonitor2,topicMonitor3
	 * introscope.epagent.stateful.topicMonitor1.class=com.wily.field.mqmonitoring.topicagent.TopicAgent -qmhost "localhost" -qmport 1414 -qmchannel "SYSTEM.DEF.SVRCONN" -qmname "MQDEVQM01" -qmuser "bezad01@TANT-A01" -qmpwd "pwd1" -pi 15
	 * introscope.epagent.stateful.topicMonitor2.class=com.wily.field.mqmonitoring.topicagent.TopicAgent -qmhost "localhost" -qmport 1415 -qmchannel "SYSTEM.DEF.SVRCONN" -qmname "MQDEVQM02" -qmuser "bezad01@TANT-A01" -qmpwd "pwd1" -pi 15
	 * introscope.epagent.stateful.topicMonitor3.class=com.wily.field.mqmonitoring.topicagent.TopicAgent -qmhost "localhost" -qmport 1416 -qmchannel "SYSTEM.DEF.SVRCONN" -qmname "MQDEVQM03" -qmuser "bezad01@TANT-A01" -qmpwd "pwd1" -pi 15
	 *
	 * Synchronization ensures that initial sequence in log file is serialized on per-plug-in basis and easy to read
	 * 
	 * @param args
	 * @param printStream
	 */
	private static void mainImpl(String[] args, PrintStream printStream){
		
		TopicAgent topicAgent = null;
		
		synchronized(TopicAgent.class){

			//epainfo("Starting TopicAgent... " + argsToString(args));
			epainfo("Starting TopicAgent... ");
			
	    	Options options = CLIFactory.getTopicAgentCLIDefinition();
			CommandLineParser parser = new DefaultParser();
			CommandLine cmdLine = null;
			  
			try {
				cmdLine = parser.parse(options, args);
			} catch (ParseException e) {
				epaerror("Error when parsing input parameters of TopicAgent.");
				CLIFactory.printHelp();
				System.exit(1);
				
			}
			
			
			try {
				topicAgent = new TopicAgent(cmdLine, printStream);
				
			} catch (Exception e) {
	            epaerror("Error when initializing TopicAgent " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
			
			epainfo("TopicAgent initialized, starting main processing loop... " + topicAgent.toString());
			
		}
		

		try {
			topicAgent.monitoringLoop();
		} catch (Exception e) {
            epaerror("Error in main monitoring loop of TopicAgent " + e.getMessage());
			e.printStackTrace();
		} 
		
		
	}
	
	/**
	 * Main method with PrintStream is called by EPAgent
	 * @param args
	 * @param printStream - stream where plug-in can write metrics for EPAgent
	 */
	public static void main(String[] args, PrintStream printStream) {
		mainImpl(args, printStream);
	}
	
	/**
	 * This main method is used for local running/debugging only.
	 * This method must use REST based metric writer 
	 * @param args
	 */
    public static void main( String[] args ) {
    	mainImpl(args, null);
    }

	@Override
	public String toString() {
		return "TopicAgent [qmHost=" + qmHost + ", qmPort=" + qmPort
				+ ", qmChannel=" + qmChannel + ", qmName=" + qmName
				+ ", qmUser=" + qmUser
				+ ", pollingInterval=" + pollingInterval + "]";
	}
    
	/**
	 * Use this method for debugging and troubleshooting only
	 * See {@link #mainImpl()}
	 * @param args
	 * @return
	 */
    @SuppressWarnings("unused")
	private static String argsToString(String[] args){
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append("[");
    	
    	for(int i = 0; i < args.length; i++){
    		sb.append(args[i]);
    		if(i != (args.length-1) ) sb.append(", ");
    	}
    	
    	sb.append("]");
    	
    	return sb.toString();
    	
    }
}