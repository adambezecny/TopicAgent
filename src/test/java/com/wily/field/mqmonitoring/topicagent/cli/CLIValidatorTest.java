package com.wily.field.mqmonitoring.topicagent.cli;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;

import org.junit.Rule;
import org.apache.commons.cli.*;
import org.junit.rules.ExpectedException;

import com.wily.field.mqmonitoring.topicagent.cli.CLIFactory;
import com.wily.field.mqmonitoring.topicagent.cli.CLIValidationException;


/**
 * 
 * @author Adam Bezecny - CA Services
 *
 */
public class CLIValidatorTest {

	
	static Options options = null;
	static CommandLineParser parser = null;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();	
	
	
	
    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("CLIValidatorTest.setUp()");
    	parser = new DefaultParser();
    	options = CLIFactory.getTopicAgentCLIDefinition();
    }
	
	
    @AfterClass
    public static void tearDown() {
    	System.out.println("CLIValidatorTest.tearDown()");
    }	

    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    	assertThat(true,is(true));
    }
	
	
	@Test
	public void testUnrecognizedOption() throws ParseException, CLIValidationException{
		
		String[] args = {"-xxx","-yyy"};

		thrown.expect(UnrecognizedOptionException.class);
		thrown.expectMessage(startsWith("Unrecognized option: -xxx"));
		@SuppressWarnings("unused")
		CommandLine cmdLine = parser.parse(options, args);
		
	}

	@Test
	public void testMissingArguments() throws ParseException, CLIValidationException{
		
		String[] args = {"-qmuser","adam", "-qmpwd", "mypassword"};

		thrown.expect(MissingOptionException.class);
		thrown.expectMessage(startsWith("Missing required option: n"));
		@SuppressWarnings("unused")
		CommandLine cmdLine = parser.parse(options, args);
		
	}

	@Test
	public void testPollingInterval1() throws ParseException, CLIValidationException{
		
		String[] args = {"-qmname","MQDEVQM01","-qmuser","adam", "-qmpwd", "mypassword","-pollingIntervalInSec","70"};

		CommandLine cmdLine = parser.parse(options, args);

		thrown.expect(CLIValidationException.class);
		thrown.expect(hasProperty("invalidReason", is(CLIValidationException.INVALID_REASON_WRONG_POLLING_INTERVAL)));
		CLIValidator.validatePollingInterval(cmdLine);
		
	}

	@Test
	public void testPollingInterval2() throws ParseException, CLIValidationException{
		
		String[] args = {"-qmname","MQDEVQM01","-qmuser","adam", "-qmpwd", "mypassword","-pi","75"};

		CommandLine cmdLine = parser.parse(options, args);
		CLIValidator.validatePollingInterval(cmdLine);
		
	}
	
    
}
