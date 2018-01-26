package com.wily.field.mqmonitoring.topicagent.metricwriter;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * Enumeration of CA APM metric types supported by EPAgent
 *
 */
public enum MetricType {
	PerIntervalCounter,
	IntCounter,
	IntAverage,
	IntRate,
	LongCounter,
	LongAverage,
	StringEvent,
	Timestamp
}
