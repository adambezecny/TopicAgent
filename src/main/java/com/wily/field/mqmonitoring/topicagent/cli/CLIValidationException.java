package com.wily.field.mqmonitoring.topicagent.cli;

/**
 * 
 * @author Adam Bezecny -  CA Services
 * 
 * This exception should primarily hold constants explaining
 * why command line is invalid. Should be thrown exclusively from
 * {@link #com.wily.field.mqmonitoring.topicagent.cli.CLIValidator}
 * Exact reason of exception is extremely useful for command line validation
 * unit testing, see {@link com.wily.field.mqmonitoring.topicagent.cli.CLIValidatorTest#testPollingInterval1()}
 *
 */
public class CLIValidationException extends Exception {

	private static final long serialVersionUID = 4299148013245906591L;

	public static final int INVALID_REASON_WRONG_POLLING_INTERVAL = 1;
	
	String message;
	int invalidReason;

	public CLIValidationException(String message){
		super(message);
	}

	public CLIValidationException(String message, int invalidReason){
		this(message);
		this.message=message;
		this.invalidReason=invalidReason;
	}

	public String getMessage() {
		return message;
	}


	public int getInvalidReason() {
		return invalidReason;
	}
	
	
}
