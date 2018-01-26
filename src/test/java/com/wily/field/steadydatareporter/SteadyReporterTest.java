package com.wily.field.steadydatareporter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.wily.field.mqmonitoring.topicagent.metricwriter.Metric;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricSet;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricType;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriterDummy;

public class SteadyReporterTest {

    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("SteadyReporterTest.setUp()");
    }
    
    @AfterClass
    public static void tearDown() {
    	System.out.println("SteadyReporterTest.tearDown()");
    }	
	
    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
	
    /**
     * This test is rather unprecise since its result highly depends
     * how LongAverageSteadyReporter cycle overlap with cycles determined
     * by Thread.sleep() calls within this method. This is in general
     * design issue of steady reported approach (or rather exact unit testing of it). 
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void testLongAverageSteadyReporter() throws InterruptedException {
    	
    	final int METRIC_FORWARDING_CYCLES = 3;
    	
    	MetricSet ms = null;
    	
    	Metric m1 = new Metric(MetricType.LongAverage, "Topic1|Publishers:Number Of Active Publishers",null);
    	Metric m2 = new Metric(MetricType.LongAverage, "Topic2|Publishers:Number Of Active Publishers",null);
    	
    	//this will initiate internal steady data manager (since polling interval > 15)
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 60);

    	SteadyDataReporterManager manager = metricWriter1.getSteadyDataReporterManager();
    	
    	LongAverageSteadyReporter reporter1 = manager.getLongAverageSteadyReporter(m1.getMetricName());
    	LongAverageSteadyReporter reporter2 = manager.getLongAverageSteadyReporter(m2.getMetricName());
    	
    	reporter1.writeLongAverage(new Long(10));
    	reporter2.writeLongAverage(new Long(12));
    	
    	ms = metricWriter1.getWritenMetrics();
    	
    	assertTrue((Long)ms.getMetricValue(m1) == 10);
    	assertTrue((Long)ms.getMetricValue(m2) == 12);

    	for(int i = 1; i <= METRIC_FORWARDING_CYCLES; i++) {

        	System.out.println("Waiting 15 sec #" + i);
        	Thread.sleep(15000);
        	
        	assertTrue((Long)ms.getMetricValue(m1) == 10);
        	assertTrue((Long)ms.getMetricValue(m2) == 12);

        	assertTrue(manager.getMapSize() == 2);
        	
        	ms.resetLongAverageMetric(m1);
        	ms.resetLongAverageMetric(m2);
    		
    	}
    	
    	System.out.println("Waiting 15 sec (1)");
    	Thread.sleep(15000);
    	
    	//values are replicated only 3 times 
    	//(i.e. METRIC_FORWARDING_CYCLES times)! 
    	//This time no further replication happened!
    	assertTrue((Long)ms.getMetricValue(m1) == 0);
    	assertTrue((Long)ms.getMetricValue(m2) == 0);

    	System.out.println("Waiting 15 sec (2)");
    	Thread.sleep(15000);
    	
    	assertTrue((Long)ms.getMetricValue(m1) == 0);
    	assertTrue((Long)ms.getMetricValue(m2) == 0);
    	
    	assertTrue(manager.getMapSize() == 0);
    	
    	
    }
}
