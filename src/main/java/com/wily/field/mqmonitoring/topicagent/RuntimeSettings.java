package com.wily.field.mqmonitoring.topicagent;

/**
 * Helper/convenience class holding common runtime settings.
 * Probably these settings should be in external configuration file
 * or provided like command line switches.
 * @author Adam Bezecny - CA Services
 *
 */
public class RuntimeSettings {

	/**
	 * Configuration metrics for Topics, Administrative Topics & Administrative subscriptions
	 * can be reported as both string literals and corresponding numerical values (determined by MQ API)
	 * This switch tells whether numerical value should be reported as well. Some customers wants only
	 * string literals to be displayed.
	 */
	public static final boolean REPORT_CONFIGURATION_METRICS_AS_INTEGER_VALUES = false;
	
}
