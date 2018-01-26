package com.wily.field.mqmonitoring.topicagent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.wily.field.mqmonitoring.topicagent.dao.MQDAOTest;
import com.wily.field.mqmonitoring.topicagent.entity.TopicStringTest;
import com.wily.field.mqmonitoring.topicagent.entity.TopicsTest;
import com.wily.field.mqmonitoring.topicagent.entity.TopicTest;
import com.wily.field.mqmonitoring.topicagent.cli.CLIValidatorTest;
import com.wily.field.mqmonitoring.topicagent.entity.AdministrativeSubscriptionTest;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterTest;
import com.wily.field.steadydatareporter.SteadyReporterTest;


/**
 * 
 * @author Adam Bezecny - CA Services
 *
 * Main unit test class defining unit test suite
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	  MQDAOTest.class,
	  MetricWriterTest.class,
	  TopicStringTest.class,
	  TopicsTest.class,
	  AdministrativeSubscriptionTest.class,
	  TopicTest.class,
	  CLIValidatorTest.class,
	  SteadyReporterTest.class
	})
public class TopicAgentTest {
	/**
	 * This class is placeholder only. It holds @RunWith &  @Suite 
	 * annotations and defines all classes of test suite.	
	 */
}
