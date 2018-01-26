package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Adam Bezecny - CA Services 
 * This class represents set of metric. Internally it utilizes HashSet to ensure each metric can be
 * present only once. If same metric is added twice, new object (metric value) will overwrite original value
 * E.g. see {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterTest#testLocalVersusClusterTopicFolder()}  
 * 
 */
public class MetricSet {

	private Set<Metric> metrics;
	
	public MetricSet(){
		this.metrics = new HashSet<Metric>();	
	}

	public Set<Metric> getWritenMetrics() {
		return metrics;
	}
	
	/**
	 * Convenience method wrapper for see {@link #addMetric(Metric)}
	 * @param metricType
	 * @param metricName
	 * @param value
	 */
	public void addMetric(MetricType metricType, String metricName, Object value){
		addMetric(new Metric(metricType, metricName, value));
	}

	/**
	 * Add metric to set of metrics maintained by this object
	 * @param metric - metric to be added
	 */
	public void addMetric(Metric metric){
		
		//if metric already exists update the value only 
		//by removing the set element first and then adding it again
		if(this.metrics.contains(metric)) 
			this.metrics.remove(metric);
			
		metrics.add(metric);
	}
	

	/**
	 * Locates metric with given type and name in metric set and updates its value with valueDiff (adds it)
	 * @param metricType metric type to search
	 * @param metricName metric name to search
	 * @param valueDiff value to add to metric once found. In order to increment current provide positive number here. 
	 * 					In order to decrement current value provide negative value here
	 */
	public void updateMetric(MetricType metricType, String metricName, int valueDiff){
		
		//value does not matter, we compare based on type and name only
		Metric metricToUpdate = new Metric(metricType, metricName, 0); 
		
		for(Metric iteratedMetric : metrics){
			if(iteratedMetric.equals(metricToUpdate)){
				iteratedMetric.setValue(((Integer)iteratedMetric.getValue()).intValue() + valueDiff);
				break;
			}
		}
	}


	/**
	 * Helper method used in {@link com.wily.field.steadydatareporter.SteadyReporterTest#testLongAverageSteadyReporter}
	 * Works with LongAverage metrics only!
	 * @param metric metric to be reset. Its value will be set to 0.
	 */
	public void resetLongAverageMetric(Metric metric){
		
		for(Metric iteratedMetric : metrics){
			if(iteratedMetric.getMetricType() == MetricType.LongAverage && iteratedMetric.equals(metric)){
				iteratedMetric.setValue(new Long(0));
				break;
			}
		}
	}
	
	
	/**
	 * returns value of metric with name & type given by metric input parameter
	 * @param metric
	 * @return
	 */
	public Object getMetricValue(Metric metric){
		
		for(Metric iteratedMetric : metrics)
			if(iteratedMetric.equals(metric)) return iteratedMetric.getValue();
		
		return null;
	}
	
	/**
	 * See {@link java.util.Set#contains}}
	 * @param o
	 * @return
	 */
	public boolean contains(Object o){
		return this.metrics.contains(o);
	}
}
