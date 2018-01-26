package com.wily.field.mqmonitoring.topicagent.metricwriter;

/**
 * 
 * @author Adam Bezecny - CA Services 
 * This class represents metric. Currently it is used for unit testing only.
 * E.g. see {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterDummy#testLocalVersusClusterTopicFolder()})
 * 
 */
public class Metric {
	
	private final MetricType metricType;
	private final String metricName;
	private Object value;

	
	public Metric(MetricType metricType, String metricName, Object value) {
		super();
		this.metricType = metricType;
		this.metricName = metricName;
		this.value = value;
	}


	
	public MetricType getMetricType() {
		return metricType;
	}

	public String getMetricName() {
		return metricName;
	}

	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return this.metricName+"="+this.value;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((metricName == null) ? 0 : metricName.hashCode());
		result = prime * result
				+ ((metricType == null) ? 0 : metricType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metric other = (Metric) obj;
		if (metricName == null) {
			if (other.metricName != null)
				return false;
		} else if (!metricName.equals(other.metricName))
			return false;
		if (metricType != other.metricType)
			return false;
		return true;
	}


}