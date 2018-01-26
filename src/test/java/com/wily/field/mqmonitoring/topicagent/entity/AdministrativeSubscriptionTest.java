package com.wily.field.mqmonitoring.topicagent.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Adam Bezecny - CA Services
 *
 */
public class AdministrativeSubscriptionTest {

	
    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("AdministrativeSubscriptionTest.setUp()");
    }
	
	
    @AfterClass
    public static void tearDown() {
    	System.out.println("AdministrativeSubscriptionTest.tearDown()");
    }	

    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }

    @Test     
    public void getSubNameToMetricTest(){
    	
    	assertTrue(new AdministrativeSubscription("id123", "A.B.C").getSubNameToMetric().equals("A.B.C"));
    	assertTrue(new AdministrativeSubscription("id123", "A|B|C").getSubNameToMetric().equals("A-B-C"));
    	assertTrue(new AdministrativeSubscription("id123", "A:B:C").getSubNameToMetric().equals("A-B-C"));
    	assertTrue(new AdministrativeSubscription("id123", "A|B:C").getSubNameToMetric().equals("A-B-C"));
    	assertTrue(new AdministrativeSubscription("id123", "A:B|C").getSubNameToMetric().equals("A-B-C"));
    	assertTrue(new AdministrativeSubscription("id123", "AdamWasHere").getSubNameToMetric().equals("AdamWasHere"));
    	
    }
    
	
}
