package com.wily.field.steadydatareporter;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static com.wily.introscope.epagent.EPALogger.*;

/**
 * Invokes constantly the reporting Thread to retrieve. 
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 * @author Adam Bezecny - CA Services
 * 
 */
public class SteadyReportingThread extends Thread {


  private final Map<String, AbstractSteadyReporter> steadyReporters;
  private int forwardLimit; 
  
  /**
   * 
   * @param steadyReporters
   * @param sleeptime
   * @param forwardLimit
   */
  public SteadyReportingThread(ConcurrentHashMap<String, AbstractSteadyReporter> steadyReporters, int forwardLimit) {
    this.steadyReporters = steadyReporters;
    this.forwardLimit = (forwardLimit * 2);//we will not replicate for forwardLimit * 15 seconds but rather forwardLimit * 2 * 7.5 seconds
  }

  public void run() {
   
	  while(true) {
    	  //System.out.println("running...");
		  ArrayList<String> cleanupList = new ArrayList<String>();
    	  
    	  try {

    			for(Map.Entry<String, AbstractSteadyReporter> entrySteadyReporter : this.steadyReporters.entrySet()) {
    				AbstractSteadyReporter iteratedSteadyReporter = entrySteadyReporter.getValue();

        	        if(iteratedSteadyReporter.getForwardCounter() < this.forwardLimit)
        	        	iteratedSteadyReporter.forwardMetric();
        	        else
        	        	cleanupList.add(iteratedSteadyReporter.getMetricPath());
    				
    			}	
    	      
    	  } catch (Exception e) {
    	      epaerror("Exception in SteadyReportingThread.run(): "+e.getMessage());
    	      e.printStackTrace();
    	  }
    	    
    	  if(cleanupList.size() > 0)
    	     cleanUpAgedMetrics(cleanupList);

	      try {
	    	/**
	    	 * We MUST sleep 15 sec when using forward counters approach!!!!
	    	 * Forward counter simply says how many subsequent 15-sec intervals 
	    	 * metric value will be replicated. If we did sleep other interval (e.g. 7.5 sec)
	    	 * replication would simply not span across all necessary intervals!  
	    	 */
	        //Thread.sleep(15000);
	    	  Thread.sleep(7500);//see constructor: this.forwardLimit = (forwardLimit * 2);  
	    	  					 //This is to better cover Thread.sleep "overlap gaps" between internal cycles of 
	    	  					//Enterprise Manager, Topic Agent & SteadyReportigThread
	      } catch (InterruptedException e) {
	    	  epaerror("InterruptedException in SteadyReportingThread.run(): "+e.getMessage());
	      }
    }
	  
  }
  
  private void cleanUpAgedMetrics(ArrayList<String> cleanupList) {
	    for(String metricName : cleanupList){
	    	this.steadyReporters.remove(metricName);
	    	//System.out.println("cleaning "+metricName);
	    }	
  }
  
}
