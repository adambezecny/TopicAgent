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
public class TopicTest {

    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("TopicTest.setUp()");
    }
	
	
    @AfterClass
    public static void tearDown() {
    	System.out.println("TopicTest.tearDown()");
    }	
	
    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
    
    @Test
    public void testSubscriptionTypes(){
    	
    	Subscription sub1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	Subscription sub2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 8, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	Subscription sub3 = new Subscription(new TopicString("Price/Fruit"), "sub3", 27, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription sub4 = new Subscription(new TopicString("Price/Fruit"), "sub4", 5, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription sub5 = new Subscription(new TopicString("Price/Fruit"), "sub5", 15, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_API);

    	Subscription sub6 = new Subscription(new TopicString("Price/Fruit"), "sub6", 23, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	Subscription sub7 = new Subscription(new TopicString("Price/Fruit"), "sub7", 34, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	Subscription sub8 = new Subscription(new TopicString("Price/Fruit"), "sub8", 2, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	Subscription sub9 = new Subscription(new TopicString("Price/Fruit"), "sub9", 7, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	
    	Topic topic = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	
    	topic.addSubscription(sub1);
    	topic.addSubscription(sub2);
    	topic.addSubscription(sub3);
    	topic.addSubscription(sub4);
    	topic.addSubscription(sub5);
    	topic.addSubscription(sub6);
    	topic.addSubscription(sub7);
    	topic.addSubscription(sub8);
    	topic.addSubscription(sub9);
    	
    	
    	assertTrue(topic.getSubscribedMessagesCount(SubscriptionType.ALL) == 131);//109 = 10+8+27+5+15+23+34+2+7
    	assertTrue(topic.getSubscriberCount(SubscriptionType.ALL) == 9);

    	assertTrue(topic.getSubscribedMessagesCount(SubscriptionType.ADMIN) == 18);//18=10+8
    	assertTrue(topic.getSubscriberCount(SubscriptionType.ADMIN) == 2);
    	
    	assertTrue(topic.getSubscribedMessagesCount(SubscriptionType.API) == 47);//47 = 27+5+15
    	assertTrue(topic.getSubscriberCount(SubscriptionType.API) == 3);
    	
    	assertTrue(topic.getSubscribedMessagesCount(SubscriptionType.PROXY) == 66);//66 = 23+34+2+7
    	assertTrue(topic.getSubscriberCount(SubscriptionType.PROXY) == 4);
    	
    }
    
    /**
     * 
     * Unit test for method {@link com.wily.field.mqmonitoring.topicagent.entity.Topic#getPublicationDelta()} 
     * 
     * 
     * Scenario	|   Publication connection  |	Published messages  | Published messages     |	Delta to be reported
     * 			|	(activeConnection)		|	(Previous Cycle)    |	(Current Cycle)	     |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 1		|	con1					|	10					|	10				     | (10-10) + (26-25) = 1
     * 			|	con2					|	25					|	26				     |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 2		|	con1					|	10					|	10				     | (10-10) = 0
     * 			|	con2					|	25					|	N/A (publisher not   | We are not considering
     * 	 		|							|						|	publishing any more	 | publisher that is not active any more	
     * -----------------------------------------------------------------------------------------------------------------------------
     * 3		|	con1					|	10					|	12				     | (12-10) = 2
     * 			|	con2					|	25					|	N/A (publisher not   | We are not considering
     * 	 		|							|						|	publishing any more	 | publisher that is not active any more	
     * -----------------------------------------------------------------------------------------------------------------------------
     * 4		|	con1					|	10					|	10				     | (10-10) + (26-0) = 26
     * 			|	con2					|	N/A (not yet active)|	26 (started 		 |
     * 			|							|						|		publishing now)	 |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 5		|	con1					|	10					|	12				     | (12-10) + (26-0) = 28
     * 			|	con2					|	N/A (not yet active)|	26 (started 		 |
     * 			|							|						|		publishing now)	 |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 5		|	con1					|	N/A					|	N/A				     | If there are no publications at all
     * 			|	con2					|	N/A 				|	N/A			 		 | delta is simply = 0
     * -----------------------------------------------------------------------------------------------------------------------------
     */
    @Test
    public void getPublicationMessageCountDeltaTest(){

    	Publication pubPrev1 = null;
    	Publication pubPrev2 = null;
    	
    	Publication pubNow1 = null;
    	Publication pubNow2 = null;
    	
    	Topic topicPrev = null;
    	Topic topicNow = null;
    	
    	
    	/*
    	 * SCENARIO #1 
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	pubPrev1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	pubPrev2 = new Publication(new TopicString("Price/Fruit"), null, null, 25, "con2");
    	
    	pubNow1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	pubNow2 = new Publication(new TopicString("Price/Fruit"), null, null, 26, "con2");
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicPrev.addOrUpdatePublication(pubPrev2);
    	topicNow.addOrUpdatePublication(pubNow1);
    	topicNow.addOrUpdatePublication(pubNow2);
    	
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 1);
    	
    	/*
    	 * SCENARIO #2
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	pubPrev1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	pubPrev2 = new Publication(new TopicString("Price/Fruit"), null, null, 25, "con2");
    	
    	pubNow1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicPrev.addOrUpdatePublication(pubPrev2);
    	topicNow.addOrUpdatePublication(pubNow1);
    	
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 0);
    	
    	/*
    	 * SCENARIO #3
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	pubPrev1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	pubPrev2 = new Publication(new TopicString("Price/Fruit"), null, null, 25, "con2");
    	
    	pubNow1 = new Publication(new TopicString("Price/Fruit"), null, null, 12, "con1");
    	
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicPrev.addOrUpdatePublication(pubPrev2);
    	topicNow.addOrUpdatePublication(pubNow1);
    	
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 2);
    	
    	
    	/*
    	 * SCENARIO #4
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	pubPrev1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	
    	
    	pubNow1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	pubNow2 = new Publication(new TopicString("Price/Fruit"), null, null, 26, "con2");
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicNow.addOrUpdatePublication(pubNow1);
    	topicNow.addOrUpdatePublication(pubNow2);
    	
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 26);

    	/*
    	 * SCENARIO #5
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	pubPrev1 = new Publication(new TopicString("Price/Fruit"), null, null, 10, "con1");
    	
    	
    	pubNow1 = new Publication(new TopicString("Price/Fruit"), null, null, 12, "con1");
    	pubNow2 = new Publication(new TopicString("Price/Fruit"), null, null, 26, "con2");
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicNow.addOrUpdatePublication(pubNow1);
    	topicNow.addOrUpdatePublication(pubNow2);
    	
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 28);

    	/*
    	 * SCENARIO #6
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	
    	//if there are no publications at all delta is simply zero
    	assertTrue(Topic.getPublicationMessageCountDelta(topicNow, topicPrev) == 0);
    	
    	
    }
	
    /**
     * 
     * Unit test for method {@link com.wily.field.mqmonitoring.topicagent.entity.Topic#getSubscriptionMessageCountDelta()} 
     * 
     * 
     * Scenario	|   Subscription ID		    |	Subscribed messages | Subscribed messages    |	Delta to be reported
     * 			|	(subscriptionId)		|	(Previous Cycle)    |	(Current Cycle)	     |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 1		|	sub1					|	10					|	10				     | (10-10) + (26-25) = 1
     * 			|	sub2					|	25					|	26				     |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 2		|	sub1					|	10					|	10				     | (10-10) = 0
     * 			|	sub2					|	25					|	N/A (subscriber not  | We are not considering
     * 	 		|							|						|	subscribing any more | publisher that is not active any more	
     * -----------------------------------------------------------------------------------------------------------------------------
     * 3		|	sub1					|	10					|	12				     | (12-10) = 2
     * 			|	sub2					|	25					|	N/A (subscriber not  | We are not considering
     * 	 		|							|						|	subscribing any more | publisher that is not active any more	
     * -----------------------------------------------------------------------------------------------------------------------------
     * 4		|	sub1					|	10					|	10				     | (10-10) + (26-0) = 26
     * 			|	sub2					|	N/A (not yet active)|	26 (started 		 |
     * 			|							|						|		subscribing now) |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 5		|	sub1					|	10					|	12				     | (12-10) + (26-0) = 28
     * 			|	sub2					|	N/A (not yet active)|	26 (started 		 |
     * 			|							|						|		subscribing now) |
     * -----------------------------------------------------------------------------------------------------------------------------
     * 5		|	sub1					|	N/A					|	N/A				     | If there are no subscriptions at all
     * 			|	sub2					|	N/A 				|	N/A			 		 | delta is simply = 0
     * -----------------------------------------------------------------------------------------------------------------------------
     */
    @Test
    public void getSubscriptionMessageCountDeltaTest(){

    	Subscription subPrev1 = null;
    	Subscription subPrev2 = null;
    	
    	Subscription subNow1 = null;
    	Subscription subNow2 = null;
    	
    	Topic topicPrev = null;
    	Topic topicNow = null;
    	
    	/*
    	 * SCENARIO #1 
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subPrev2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 25, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subNow2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 26, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	topicPrev.addSubscription(subPrev1);
    	topicPrev.addSubscription(subPrev2);
    	topicNow.addSubscription(subNow1);
    	topicNow.addSubscription(subNow2);
    	
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 1);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 1);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);
    	
    	/*
    	 * SCENARIO #2
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subPrev2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 25, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	
    	topicPrev.addSubscription(subPrev1);
    	topicPrev.addSubscription(subPrev2);
    	topicNow.addSubscription(subNow1);
    	
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);
    	
    	
    	/*
    	 * SCENARIO #3
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subPrev2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 25, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 12, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	
    	topicPrev.addSubscription(subPrev1);
    	topicPrev.addSubscription(subPrev2);
    	topicNow.addSubscription(subNow1);
    	
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 2);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 2);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);
    	
    	
    	/*
    	 * SCENARIO #4
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	
    	subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subNow2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 26, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	topicPrev.addSubscription(subPrev1);
    	topicNow.addSubscription(subNow1);
    	topicNow.addSubscription(subNow2);
    	
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 26);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 26);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);

    	/*
    	 * SCENARIO #5
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");

    	subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	
    	subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 12, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	subNow2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 26, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	
    	topicPrev.addSubscription(subPrev1);
    	topicNow.addSubscription(subNow1);
    	topicNow.addSubscription(subNow2);
    	
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 28);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 28);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);

    	/*
    	 * SCENARIO #6
    	 */
    	topicPrev = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	topicNow  = new Topic(new TopicString("Price/Fruit"), "Price.Fruit");
    	
    	//if there are no publications at all delta is simply zero
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ALL) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.ADMIN) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.API) == 0);
    	assertTrue(Topic.getSubscriptionMessageCountDelta(topicNow, topicPrev, SubscriptionType.PROXY) == 0);
    	
    	
    }
    
}
