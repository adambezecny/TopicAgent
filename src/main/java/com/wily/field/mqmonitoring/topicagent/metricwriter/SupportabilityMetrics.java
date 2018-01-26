package com.wily.field.mqmonitoring.topicagent.metricwriter;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * Enumeration of supportability metrics provided by the implementation.
 * Used in: {@link com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter#writeSupportabilityMetric()}
 *
 */
public enum SupportabilityMetrics {

	PCF_SEND_MESSAGE_TIME,
	PCF_SEND_MESSAGE_ERROR,
	PCF_DISCONNECT_ERROR,
	PCF_INQUIRE_TOPIC_STATUS_TIME,
	PCF_INQUIRE_TOPIC_STATUS_SUB_TIME,
	PCF_INQUIRE_TOPIC_STATUS_PUB_TIME,
	PCF_INQUIRE_SUBSCRIPTION_TIME,
	PCF_INQUIRE_TOPIC_TIME,
	PCF_INQUIRE_TOPIC_NAMES_TIME,
	MQ_DATA_FETCH_TIME,
	MQ_DATA_METRIC_WRITE_TIME
}
