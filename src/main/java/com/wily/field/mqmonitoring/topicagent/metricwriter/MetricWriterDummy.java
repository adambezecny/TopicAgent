package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.util.Date;

import com.wily.field.steadydatareporter.SteadyDataReporterManager;

/**
 * 
 * @author Adam Bezecny - CA Services
 *
 * Dummy metric writer writes metrics into Standard Output only. 
 * Primary purpose of MetricWriterDummy is to be used in unit tests 
 * (to check which metrics were generated and if they have correct values).
 * E.g. see {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterTest#testSubscriptionMetrics1()}
 * It can be also used for debugging purposes. 
 */
public class MetricWriterDummy extends MetricWriter {

	/**
	 * This is the list of all metrics written by this metric writer. 
	 * Primary purpose is to use it during unit testing, i.e. to check 
  	 * what metrics (and with what values) were written during processing
	 * 
	 */
	private MetricSet writenMetrics; 
	
	public MetricWriterDummy(String queueManager, int pollingInterval) {
		super(queueManager, pollingInterval);
		
		this.writenMetrics = new MetricSet();
	}

	/**
	 * Use this getter method to access list of metrics
	 * written during processing in respective unit test method(s)
	 * @return
	 */
	public MetricSet getWritenMetrics() {
		return this.writenMetrics;
	}
	
	/**
	 * Normally steady data manager should be used only within abstract class
	 * MetricWriter where it is defined and used. Since dummy metric writer is
	 * used in unit tests where we need to access it this is the convenience method
	 * that can be used to access steady data manager
	 * @return
	 */
	public SteadyDataReporterManager getSteadyDataReporterManager() {
		return this.steadyDataManager;
	}
	

	@Override
	public void writeTimestamp(String metric, Date value) {
		//System.out.println("writeTimestamp: "+metric+"="+value);
		writenMetrics.addMetric(MetricType.Timestamp, metric, value);
		
	}

	@Override
	public void writeLongAverage(String metric, long value) {
		//System.out.println("writeLongAverage: "+metric+"="+value);
		writenMetrics.addMetric(MetricType.LongAverage, metric, value);
	}

	@Override
	public void writeErrorDetectorEntry(String subject, String errorEntry) {
		//System.out.println("writeErrorDetectorEntry: subject="+subject+", errorEntry="+errorEntry);
	}

	@Override
	public void writePerIntervalCounter(String metric) {
		//System.out.println("writePerIntervalCounter: "+metric);
		writenMetrics.addMetric(MetricType.PerIntervalCounter, metric, 1);
	}
	
	@Override
	public void writePerIntervalCounter(String metric, int value) {
		//System.out.println("writePerIntervalCounter: "+metric+"="+value);
		writenMetrics.addMetric(MetricType.PerIntervalCounter, metric, value);
	}
	

	@Override
	public void forceExistPerIntervalCounter(String metric) {
		return;	
	}

	@Override
	public void forceExistLongIntervalCounter(String metric) {
		return;	
	}

	@Override
	public void writeLongCounter(String metric, long value) {
		//System.out.println("writeLongCounter: "+metric+"="+value);	
		writenMetrics.addMetric(MetricType.LongCounter, metric, value);
	}

	@Override
	public void writeIntCounter(String metric, int value) {
		//System.out.println("writeIntCounter: "+metric+"="+value);	
		writenMetrics.addMetric(MetricType.IntCounter, metric, value);
	}

	@Override
	public void decrementCounter(String metric, int value) {
		//System.out.println("decrementCounter: "+metric+"="+value);	
		writenMetrics.updateMetric(MetricType.IntCounter, metric, (-1) * value);
	}

	@Override
	public void incrementCounter(String metric, int value) {
		//System.out.println("incrementCounter: "+metric+"="+value);	
		writenMetrics.updateMetric(MetricType.IntCounter, metric, value);
	}

	@Override
	public void writeString(String metric, String value) {
		//System.out.println("writeString: "+metric+"="+value);	
		writenMetrics.addMetric(MetricType.StringEvent, metric, value);
	}

}