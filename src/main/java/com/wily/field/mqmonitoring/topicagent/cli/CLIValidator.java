package com.wily.field.mqmonitoring.topicagent.cli;

import org.apache.commons.cli.*;

/**
 * 
 * @author Adam Bezecny -  CA Services
 *
 * Put command line validation methods here. These methods should be 
 * called from {@link #com.wily.field.mqmonitoring.topicagent.TopicAgent}
 * constructor once command line is parsed.
 *
 */
public class CLIValidator {

	public static void validatePollingInterval(CommandLine cmdLine) throws CLIValidationException {
		
		int pollingInterval = Integer.parseInt(cmdLine.getOptionValue(CLIFactory.CLI_OPT_QM_POLLING_INTERVAL, "15"));
		
		if( (pollingInterval < 15) || (pollingInterval % 15 != 0) )
			throw new CLIValidationException("TopicAgent polling interval must be positive integer ( >= 15) dividable by 15!", 
					CLIValidationException.INVALID_REASON_WRONG_POLLING_INTERVAL);
		
	}	
	
}
