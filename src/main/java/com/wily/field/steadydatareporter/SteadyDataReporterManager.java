package com.wily.field.steadydatareporter;

import java.util.concurrent.ConcurrentHashMap;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;

/**
 * 
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 * @author Adam Bezecny - CA Services
 *
 */
public class SteadyDataReporterManager {

  
  private final SteadyReportingThread reportingThread;
  private ConcurrentHashMap<String, AbstractSteadyReporter> steadyReporters;
  
  private MetricWriter metricWriter = null;

  /**
   * 
   * @param metricWriter
   * @param sleepTime
   * @param forwardLimit
   */
  public SteadyDataReporterManager(MetricWriter metricWriter, int forwardLimit) {
	
	this.metricWriter = metricWriter;  
	this.steadyReporters = new ConcurrentHashMap<String, AbstractSteadyReporter>();
	this.reportingThread = new SteadyReportingThread(steadyReporters, forwardLimit);
    
	Thread runningReportThread = new Thread(reportingThread);
    runningReportThread.start();
  }

  /**
   * Register a metric (SteadyReporter)
   * @param steadyReporter
   */
  private void registerSteadyReporter(AbstractSteadyReporter steadyReporter) {
    
    if (!steadyReporters.containsKey(steadyReporter.getMetricPath())) {
      steadyReporters.put(steadyReporter.getMetricPath(), steadyReporter);
    }
  }
  
  /**
   * 
   * @param metricname
   * @return the SteadyReporter for a certain metric
   */
  public final LongAverageSteadyReporter getLongAverageSteadyReporter(String metricname) {
    LongAverageSteadyReporter steadyReporter = (LongAverageSteadyReporter) steadyReporters.get(metricname);
    if (steadyReporter == null) {
      steadyReporter = new LongAverageSteadyReporter(metricname, this.metricWriter);
      registerSteadyReporter(steadyReporter);
    }
    return steadyReporter;
  }

  /**
   * Helper method used for unit tests only!
   */
  protected int getMapSize(){
	  return this.steadyReporters.size();
  }
  
}