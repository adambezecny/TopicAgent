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
public class TopicStringTest {

    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("TopicStringTest.setUp()");
    }
	
	
    @AfterClass
    public static void tearDown() {
    	System.out.println("TopicStringTest.tearDown()");
    }	

    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
    
    @Test
    public void toMetricPathTest(){
    	
    	assertTrue(new TopicString("/A/B/C").toMetricPath().equals("A|B|C"));
    	assertTrue(new TopicString("A/B/C").toMetricPath().equals("A|B|C"));
    	assertTrue(new TopicString("News").toMetricPath().equals("News"));
    	assertTrue(new TopicString("").toMetricPath().equals(""));

    	TopicString ts = new TopicString("/A/B/C");
    	assertTrue(ts.toMetricPath().equals("A|B|C"));
    	assertTrue(ts.getTopicString().equals("/A/B/C"));
    }
    
    @Test
    public void getUnwildcardedParentTopicTest(){
    	
    	assertTrue(new TopicString("/A/B/C").getUnwildcardedParentTopic().equals("/A/B/C"));
    	assertTrue(new TopicString("A/B/C").getUnwildcardedParentTopic().equals("A/B/C"));
    	assertTrue(new TopicString("A/B/#").getUnwildcardedParentTopic().equals("A/B"));
    	assertTrue(new TopicString("A/B/*").getUnwildcardedParentTopic().equals("A/B"));
    	
    	assertTrue(new TopicString("A/B/C#").getUnwildcardedParentTopic().equals("A/B"));
    	assertTrue(new TopicString("A/B/C*").getUnwildcardedParentTopic().equals("A/B"));
    	
    	
    	assertTrue(new TopicString("A/#/C").getUnwildcardedParentTopic().equals("A"));
    	assertTrue(new TopicString("A/*/C").getUnwildcardedParentTopic().equals("A"));   
    	assertTrue(new TopicString("A/*/C/#/D").getUnwildcardedParentTopic().equals("A"));
    	assertTrue(new TopicString("A/#/C/*/D").getUnwildcardedParentTopic().equals("A"));
    	
    	assertTrue(new TopicString("A/X*/C/Y#/D").getUnwildcardedParentTopic().equals("A"));
    	assertTrue(new TopicString("A/X#/C/Y*/D").getUnwildcardedParentTopic().equals("A"));
    	
    	
    }
    
}

