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
public class TopicsTest {

    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("TopicsTest.setUp()");
    }
	
	
    @AfterClass
    public static void tearDown() {
    	System.out.println("TopicsTest.tearDown()");
    }	

    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
	
    @Test
    public void testTopicsClass(){
    	Topic t1 = new Topic(new TopicString("Price"), null);
    	Topic t2 = new Topic(new TopicString("Price/Fruit"),"Price.Fruit");
    	Topic t3 = new Topic(new TopicString("Price/Vegetables"),"Price.Vegetables");
    	
    	Topics topics = new Topics();
    	topics.addTopic(t1);
    	topics.addTopic(t2);
    	topics.addTopic(t3);
    	
    	assertTrue(topics.getTopicByTopicString("Price").equals(t1));
    	assertTrue(topics.getTopicByTopicString("Price/Fruit").equals(t2));
    	assertTrue(topics.getTopicByTopicString("Price/Vegetables").equals(t3));
    	
    }
    
}
