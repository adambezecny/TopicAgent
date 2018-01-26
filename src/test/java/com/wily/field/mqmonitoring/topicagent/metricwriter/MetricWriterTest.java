package com.wily.field.mqmonitoring.topicagent.metricwriter;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ibm.mq.MQException;
import com.wily.field.mqmonitoring.topicagent.entity.AdministrativeTopic;
import com.wily.field.mqmonitoring.topicagent.entity.Publication;
import com.wily.field.mqmonitoring.topicagent.entity.Subscription;
import com.wily.field.mqmonitoring.topicagent.entity.Topic;
import com.wily.field.mqmonitoring.topicagent.entity.TopicString;
import com.wily.field.mqmonitoring.topicagent.entity.Topics;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricWriter;
import com.wily.field.mqmonitoring.topicagent.metricwriter.MetricSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.*;

/**
 * 
 * @author Adam Bezecny - CA Services
 *
 */
public class MetricWriterTest {
	
    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println("MetricWriterTest.setUp()");
    }
    
    @AfterClass
    public static void tearDown() {
    	System.out.println("MetricWriterTest.tearDown()");
    }	
	
    @Ignore
    @Test
    public void testTemplate() {
    	assertTrue(true);
    	assertFalse(false);
    }
   
    /**
     * Helper method used to bypass full object constructors (where all the values need to be specified).
     * Instead lightweight constructor is being called 
     * (e.g. {@link com.wily.field.mqmonitoring.topicagent.entity.Topic#Topic(TopicString, String)})
     * and private parameters necessary for unit test are setup suing this method
     * @param object Object to setup
     * @param fieldName private parameter name to setup
     * @param value value to setup
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
    	
    	Field field = object.getClass().getDeclaredField(fieldName);
    	field.setAccessible(true);
    	field.set(object, value);
    	field.setAccessible(false);
    }
    
    
    @Test
    public void metricSetTest(){
    	MetricSet ms = new MetricSet();
    	
    	Metric m = new Metric(MetricType.StringEvent, "A|B|C:D","AdamWasHere1");
    	ms.addMetric(m);
    	
    	assertTrue(ms.getMetricValue(m).equals("AdamWasHere1"));
    	assertTrue(ms.getWritenMetrics().size() == 1);
    	
    	Metric m2 = new Metric(MetricType.StringEvent, "A|B|C:D","AdamWasHere2");
    	ms.addMetric(m2);
    	
    	assertTrue(ms.getMetricValue(m).equals("AdamWasHere2"));
    	assertTrue(ms.getMetricValue(m2).equals("AdamWasHere2"));
    	assertTrue(ms.getWritenMetrics().size() == 1);
    	
    	Metric m3 = new Metric(MetricType.StringEvent, "A|B|C|X:D","AdamWasHere3");
    	
    	ms.addMetric(m3);
    	
    	
    	assertTrue(ms.getMetricValue(m).equals("AdamWasHere2"));
    	assertTrue(ms.getMetricValue(m2).equals("AdamWasHere2"));
    	assertTrue(ms.getMetricValue(m3).equals("AdamWasHere3"));
    	
    	assertTrue(ms.getWritenMetrics().size() == 2);
    	
    }
    
    @Test
    public void normalizeQueueManagerNameTest(){
    	String qmName = null;
    	
    	qmName = MetricWriter.normalizeQueueManagerName("MQDEVQM01");
    	assertEquals("MQDEVQM01", qmName);
    	
    	qmName = MetricWriter.normalizeQueueManagerName("MQDEVQM01:ABC:DD/EE");
    	assertEquals("MQDEVQM01-ABC-DD-EE", qmName);    	
    	
    }
    

    /**
     * This test requires CA APM EPA agent to be running 
     * and listening on port 9777 for HTTP POST requests
     * @throws Exception
     */
    @Ignore
    @Test
    public void testRESTMetricWriter() throws Exception{
    	
    	 //for this to work setup introscope.epagent.config.httpServerPort  in IntroscopeEPAgent.properties (setup value 9777)
    	 MetricWriter metricWriter = new MetricWriterREST("MQDEVQM01", "http://localhost:9777/apm/metricFeed", 15); 
    	 metricWriter.writeString("ADAM|WAS|HERE:Metric123", "Work Sucks!");
    }
    
    
    /**
     * Tests whether local and cluster topics are displayed in metric tree under correct folder:
     * 		Local/non-shared topics must be under 'Local Topics' folder
     * 		Clustered topics defined locally must be under 'Local Topics' folder
     * 		Clustered topics defined remotely must be under 'Cluster Topics' folder
     * @throws MQException
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public void testLocalVersusClusterTopicFolder() throws MQException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	MetricWriterDummy metricWriter2 = new MetricWriterDummy("MQDEVQM02", 15);
    	
    	Topics topics = new Topics();
    	
    	Topic topic1 = new Topic(new TopicString("Price/Fruit"), "Price.Fruit"); 
    	AdministrativeTopic atopic1 = new AdministrativeTopic("Price.Fruit", 1,	"MQTOPT_CLUSTER", "MY_CLUSTER");
    	
    	Topic topic2 = new Topic(new TopicString("Price/Vegetables"), "Price.Vegetables"); 
    	AdministrativeTopic atopic2 = new AdministrativeTopic("Price.Vegetables", 0, "MQTOPT_LOCAL", null);
    	
    	
    	setPrivateField(topic1, "administrativeTopic", atopic1);
    	setPrivateField(atopic1, "qmgrName", "MQDEVQM01");
    	
    	setPrivateField(topic2, "administrativeTopic", atopic2);
    	
    	
    	topics.addTopic(topic1);
    	topics.addTopic(topic2);
    	
    	metricWriter1.generateTopicMetrics(topics, null);
    	metricWriter2.generateTopicMetrics(topics, null);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	MetricSet metrics2 = metricWriter2.getWritenMetrics();
    	
    	Metric metricTopicNameL = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_AdminTopic|Local Topic|Configuration:TopicName", null); 
    	Metric metricTopicNameC = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_AdminTopic|Cluster Topic|Configuration:TopicName", null);

    	Metric metricTopicName2L = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|_AdminTopic|Local Topic|Configuration:TopicName", null); 
    	Metric metricTopicName2C = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Fruit|_AdminTopic|Cluster Topic|Configuration:TopicName", null);
    	
    	Metric metricTopicName3L = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_AdminTopic|Local Topic|Configuration:TopicName", null);
    	Metric metricTopicName4L = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Vegetables|_AdminTopic|Local Topic|Configuration:TopicName", null);
    	Metric metricTopicName3C = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_AdminTopic|Cluster Topic|Configuration:TopicName", null);
    	Metric metricTopicName4C = new Metric(MetricType.StringEvent, "MQ Topic Monitor|MQDEVQM02|Topic Space|Price|Vegetables|_AdminTopic|Cluster Topic|Configuration:TopicName", null);

    	
    	assertTrue( metrics.contains(metricTopicNameL) );//cluster topic on local queue manager must be listed under Local Topics!
    	assertFalse( metrics.contains(metricTopicNameC) );
    	

    	assertTrue( metrics2.contains(metricTopicName2C) );//cluster topic on remote queue manager must be listed under Cluster Topics!
    	assertFalse( metrics2.contains(metricTopicName2L) );
    	
    	assertTrue( metrics.contains(metricTopicName3L) );//non clustered/non shared topic is under Local Topics
    	assertTrue( metrics2.contains(metricTopicName4L) );
    	assertFalse( metrics.contains(metricTopicName3C) );
    	assertFalse( metrics2.contains(metricTopicName4C) );
    	
    	
    }
    
    /**
     * Test whether publication metrics are reported correctly in two cycles (i.e. also delta metrics' calculation)
     */
    @Test
    public void testPublicationMetrics1() {
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	
    	Publication pubNow1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:15", 10, "connection123");
    	Publication pubNow2 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:19", 25, "connection456");
    	
    	Publication pubPrev1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:26:55", 3, "connection123");
    	Publication pubPrev2 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:26:56", 4, "connection456");
    	
    	
    	Topic topic1 = new Topic(new TopicString("Price/Vegetables"), null);//no admin topic defined, hence adminTopicName = null
    	Topic topic2 = new Topic(new TopicString("Price/Vegetables"), null);
    	
    	Topics topicsNow = new Topics();//current cycle
    	Topics topicsPrev = new Topics();//previous cycle
    	
    	topic1.addOrUpdatePublication(pubNow1);
    	topic1.addOrUpdatePublication(pubNow2);

    	topic2.addOrUpdatePublication(pubPrev1);
    	topic2.addOrUpdatePublication(pubPrev2);
    	
    	
    	topicsNow.addTopic(topic1);
    	topicsPrev.addTopic(topic2);
    	
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	
    	
    	Metric metricActivePublishers = new Metric(MetricType.LongAverage, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Number Of Active Publishers", null);
    	Metric metricActivePublishedMessages = new Metric(MetricType.IntCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Total", null);
    	Metric metricActivePublishedMessagesDelta = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Per Interval", null);
    	
    	assertTrue(metrics.contains(metricActivePublishers));
    	assertTrue(metrics.contains(metricActivePublishedMessages));
    	assertTrue(metrics.contains(metricActivePublishedMessagesDelta));
    	
    	
    	assertTrue((Long)metrics.getMetricValue(metricActivePublishers) == 2);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessages) == 35);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessagesDelta) == 28); //28 = (10+25) - (3+4)
    	
    }
    
    /**
     * Tests specific use case when publications are added only in the second cycle, 
     * i.e. no publications present (for given topic) in first cycle at all 
     */
    @Test
    public void testPublicationMetrics2() {
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	
    	Topics topicsNow = new Topics();//current cycle
    	Topics topicsPrev = new Topics();//previous cycle
    	
    	Publication pubNow1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:15", 10, "connection123");
    	Publication pubNow2 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:19", 25, "connection456");
    	
    	Topic topic1 = new Topic(new TopicString("Price/Vegetables"), null);//no admin topic defined, hence adminTopicName = null
    	Topic topic2 = new Topic(new TopicString("Price/Vegetables"), null);
    	
    	topic1.addOrUpdatePublication(pubNow1);//add publications in current cycle only, no publications in previous cycle
    	topic1.addOrUpdatePublication(pubNow2);
    	
    	topicsNow.addTopic(topic1);
    	topicsPrev.addTopic(topic2);
    	
    	
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	
    	Metric metricActivePublishers = new Metric(MetricType.LongAverage, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Number Of Active Publishers", null);
    	Metric metricActivePublishedMessages = new Metric(MetricType.IntCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Total", null);
    	Metric metricActivePublishedMessagesDelta = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Per Interval", null);
    	
    	assertTrue(metrics.contains(metricActivePublishers));
    	assertTrue(metrics.contains(metricActivePublishedMessages));
    	assertTrue(metrics.contains(metricActivePublishedMessagesDelta));
    	
    	assertTrue((Long)metrics.getMetricValue(metricActivePublishers) == 2);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessages) == 35);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessagesDelta) == 35); //35 = (10+25) - (0+0)
    	
    	
    }

    /**
     * Similar to {@link #testPublicationMetrics2}} 
     * In this case previous cycle does not even contain corresponding topic
     *  
     */
    @Test
    public void testPublicationMetrics3() {
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	
    	Topics topicsNow = new Topics();//current cycle
    	Topics topicsPrev = new Topics();//previous cycle
    	
    	Publication pubNow1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:15", 10, "connection123");
    	Publication pubNow2 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:19", 25, "connection456");
    	
    	Topic topic1 = new Topic(new TopicString("Price/Vegetables"), null);//no admin topic defined, hence adminTopicName = null
    	Topic topic2 = new Topic(new TopicString("Price/Fruit"), null);//completely different topic in previous cycle
    	
    	topic1.addOrUpdatePublication(pubNow1);//add publications in current cycle only, no publications in previous cycle
    	topic1.addOrUpdatePublication(pubNow2);
    	
    	topicsNow.addTopic(topic1);
    	topicsPrev.addTopic(topic2);
    	
    	
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	
    	Metric metricActivePublishers = new Metric(MetricType.LongAverage, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Number Of Active Publishers", null);
    	Metric metricActivePublishedMessages = new Metric(MetricType.IntCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Total", null);
    	Metric metricActivePublishedMessagesDelta = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Per Interval", null);
    	
    	assertTrue(metrics.contains(metricActivePublishers));
    	assertTrue(metrics.contains(metricActivePublishedMessages));
    	assertFalse(metrics.contains(metricActivePublishedMessagesDelta));//no delta metric reported! there was no corresponding topic in previous cycle
    																	  //this is very first time given topic (Price/Vegetables) is reported!
    	
    	assertTrue((Long)metrics.getMetricValue(metricActivePublishers) == 2);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessages) == 35);
    	
    }
    
    /**
     * This scenario represents use case when current cycle does not contain publication that was present in previous cycle
     * E.g.:
     * 
     * Cycle1(Prev.):
     * 
     * pub1
     * pub2
     * 
     * Cycle2(Now):
     * 
     * pub1
     * 
     */
    @Test
    public void testPublicationMetrics4() {
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	
    	Topics topicsNow = new Topics();//current cycle
    	Topics topicsPrev = new Topics();//previous cycle
    	
    	Publication pubNow1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:15", 10, "connection123");

    	Publication pubPrev1 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:15", 10, "connection123");
    	Publication pubPrev2 = new Publication(new TopicString("Price/Vegetables"), "2016-11-23", "18:27:19", 25, "connection456");
    	
    	Topic topicNow = new Topic(new TopicString("Price/Vegetables"), null);
    	Topic topicPrev = new Topic(new TopicString("Price/Vegetables"), null);
    	
    	topicNow.addOrUpdatePublication(pubNow1);
    	
    	topicPrev.addOrUpdatePublication(pubPrev1);
    	topicPrev.addOrUpdatePublication(pubPrev2);
    	
    	topicsNow.addTopic(topicNow);
    	topicsPrev.addTopic(topicPrev);
    	
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	
    	Metric metricActivePublishers = new Metric(MetricType.LongAverage, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Number Of Active Publishers", null);
    	Metric metricActivePublishedMessages = new Metric(MetricType.IntCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Total", null);
    	Metric metricActivePublishedMessagesDelta = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Vegetables|_Publications:Published Messages Per Interval", null);
    	
    	assertTrue(metrics.contains(metricActivePublishers));
    	assertTrue(metrics.contains(metricActivePublishedMessages));
    	assertTrue(metrics.contains(metricActivePublishedMessagesDelta));
    	
    	assertTrue((Long)metrics.getMetricValue(metricActivePublishers) == 1);
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessages) == 10);
    	
    	//0 = 10(now) - 10(prev); we do not include 25(prev) since there is no publication 
    	//with same connectionId (connection456) in current cycle!!!
    	assertTrue((Integer)metrics.getMetricValue(metricActivePublishedMessagesDelta) == 0); 
    	
    }
    
    @Test
    public void testSubscriptionMetrics1() {
    	
    	MetricWriterDummy metricWriter1 = new MetricWriterDummy("MQDEVQM01", 15);
    	Topics topicsNow = new Topics();
    	Topics topicsPrev = null;
    	
    	Subscription subNow1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 10, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	Subscription subNow2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 8, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);

    	Subscription subNow3 = new Subscription(new TopicString("Price/Fruit"), "sub3", 27, "1111111", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription subNow4 = new Subscription(new TopicString("Price/Fruit"), "sub4", 5, "2222222", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription subNow5 = new Subscription(new TopicString("Price/Fruit"), "sub5", 15, "3333333", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	
    	Subscription subNow6 = new Subscription(new TopicString("Price/Fruit"), "sub6", 23, "4444444", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	
    	
    	Subscription subPrev1 = new Subscription(new TopicString("Price/Fruit"), "sub1", 7, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);
    	Subscription subPrev2 = new Subscription(new TopicString("Price/Fruit"), "sub2", 8, "0000000", Subscription.SUBSCRIPTION_TYPE_STR_ADMIN);

    	Subscription subPrev3 = new Subscription(new TopicString("Price/Fruit"), "sub3", 20, "1111111", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription subPrev4 = new Subscription(new TopicString("Price/Fruit"), "sub4", 5, "2222222", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	Subscription subPrev5 = new Subscription(new TopicString("Price/Fruit"), "sub5", 10, "3333333", Subscription.SUBSCRIPTION_TYPE_STR_API);
    	
    	Subscription subPrev6 = new Subscription(new TopicString("Price/Fruit"), "sub6", 15, "4444444", Subscription.SUBSCRIPTION_TYPE_STR_PROXY);
    	
    	
    	Topic topicNow = new Topic(new TopicString("Price/Fruit"), null);//topic name = null, we do not need administrative topic object for this test
    	Topic topicPrev = new Topic(new TopicString("Price/Fruit"), null);
    	
    	topicNow.addSubscription(subNow1);
    	topicNow.addSubscription(subNow2);
    	topicNow.addSubscription(subNow3);
    	topicNow.addSubscription(subNow4);
    	topicNow.addSubscription(subNow5);
    	topicNow.addSubscription(subNow6);

    	
    	topicsNow.addTopic(topicNow);
    	
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	
    	MetricSet metrics = metricWriter1.getWritenMetrics();
    	
    	Metric subscriptionsActiveSubscribers 	= new Metric(MetricType.LongAverage, 		"MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions:Number Of Active Subscribers", null);
    	Metric subscriptionsMessagesPerInterval = new Metric(MetricType.PerIntervalCounter,	"MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions:Number Of Messages Per Interval", null);
    	Metric subscriptionsMessagesTotal 		= new Metric(MetricType.IntCounter, 		"MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions:Number Of Messages Total", null);

    	Metric apiSubscriptionsActiveSubscribers    = new Metric(MetricType.LongAverage, 	   "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|API Subscriptions:Number Of Active Subscribers", null);
    	Metric apiSubscriptionsMessagesPerInterval  = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|API Subscriptions:Number Of Messages Per Interval", null);
    	Metric apiSubscriptionsMessagesTotal 	    = new Metric(MetricType.IntCounter, 	    "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|API Subscriptions:Number Of Messages Total", null);
    	
    	Metric adminSubscriptionsSubscribers    	 = new Metric(MetricType.LongAverage, 	     "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Administrative Subscriptions:Number Of Active Subscribers", null);
    	Metric adminSubscriptionsMessagesPerInterval = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Administrative Subscriptions:Number Of Messages Per Interval", null);
    	Metric adminSubscriptionsMessagesTotal 	     = new Metric(MetricType.IntCounter, 		 "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Administrative Subscriptions:Number Of Messages Total", null);

    	Metric proxySubscriptionsActiveSubscribers    = new Metric(MetricType.LongAverage, 	     "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Proxy Subscriptions:Number Of Active Subscribers", null);
    	Metric proxySubscriptionsMessagesPerInterval = new Metric(MetricType.PerIntervalCounter, "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Proxy Subscriptions:Number Of Messages Per Interval", null);
    	Metric proxySubscriptionsMessagesTotal 	     = new Metric(MetricType.IntCounter, 		 "MQ Topic Monitor|MQDEVQM01|Topic Space|Price|Fruit|_Subscriptions|Proxy Subscriptions:Number Of Messages Total", null);
    	
    	assertTrue(metrics.contains(subscriptionsActiveSubscribers));
    	assertFalse(metrics.contains(subscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(subscriptionsMessagesTotal));
    	
    	assertTrue(metrics.contains(apiSubscriptionsActiveSubscribers));
    	assertFalse(metrics.contains(apiSubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(apiSubscriptionsMessagesTotal));

    	assertTrue(metrics.contains(adminSubscriptionsSubscribers));
    	assertFalse(metrics.contains(adminSubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(adminSubscriptionsMessagesTotal));

    	assertTrue(metrics.contains(proxySubscriptionsActiveSubscribers));
    	assertFalse(metrics.contains(proxySubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(proxySubscriptionsMessagesTotal));
    	
    	
    	assertTrue((Long)metrics.getMetricValue(subscriptionsActiveSubscribers) == 6);
    	assertTrue((Long)metrics.getMetricValue(apiSubscriptionsActiveSubscribers) == 3);
    	assertTrue((Long)metrics.getMetricValue(adminSubscriptionsSubscribers) == 2);
    	assertTrue((Long)metrics.getMetricValue(proxySubscriptionsActiveSubscribers) == 1);
    	
    	assertTrue((Integer)metrics.getMetricValue(subscriptionsMessagesTotal) == 88);//88 = 10+8+27+5+15+23
    	assertTrue((Integer)metrics.getMetricValue(apiSubscriptionsMessagesTotal) == 47);//47 = 27+5+15
    	assertTrue((Integer)metrics.getMetricValue(adminSubscriptionsMessagesTotal) == 18);//18 = 10+8
    	assertTrue((Integer)metrics.getMetricValue(proxySubscriptionsMessagesTotal) == 23);//23
    	
    	/*
    	 * Simulate second cycle
    	 */
    	
    	topicPrev.addSubscription(subPrev1);
    	topicPrev.addSubscription(subPrev2);
    	topicPrev.addSubscription(subPrev3);
    	topicPrev.addSubscription(subPrev4);
    	topicPrev.addSubscription(subPrev5);
    	topicPrev.addSubscription(subPrev6);
    	
    	topicsPrev = new Topics();
    	topicsPrev.addTopic(topicPrev);
    	metricWriter1.generateTopicMetrics(topicsNow, topicsPrev);
    	metrics = metricWriter1.getWritenMetrics();
    	
    	assertTrue(metrics.contains(subscriptionsActiveSubscribers));
    	assertTrue(metrics.contains(subscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(subscriptionsMessagesTotal));
    	
    	assertTrue(metrics.contains(apiSubscriptionsActiveSubscribers));
    	assertTrue(metrics.contains(apiSubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(apiSubscriptionsMessagesTotal));

    	assertTrue(metrics.contains(adminSubscriptionsSubscribers));
    	assertTrue(metrics.contains(adminSubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(adminSubscriptionsMessagesTotal));

    	assertTrue(metrics.contains(proxySubscriptionsActiveSubscribers));
    	assertTrue(metrics.contains(proxySubscriptionsMessagesPerInterval));
    	assertTrue(metrics.contains(proxySubscriptionsMessagesTotal));
    	
    	assertTrue((Long)metrics.getMetricValue(subscriptionsActiveSubscribers) == 6);
    	assertTrue((Long)metrics.getMetricValue(apiSubscriptionsActiveSubscribers) == 3);
    	assertTrue((Long)metrics.getMetricValue(adminSubscriptionsSubscribers) == 2);
    	assertTrue((Long)metrics.getMetricValue(proxySubscriptionsActiveSubscribers) == 1);
    	
    	assertTrue((Integer)metrics.getMetricValue(subscriptionsMessagesTotal) == 88);//88 = 10+8+27+5+15+23
    	assertTrue((Integer)metrics.getMetricValue(apiSubscriptionsMessagesTotal) == 47);//47 = 27+5+15
    	assertTrue((Integer)metrics.getMetricValue(adminSubscriptionsMessagesTotal) == 18);//18 = 10+8
    	assertTrue((Integer)metrics.getMetricValue(proxySubscriptionsMessagesTotal) == 23);//23

    	assertTrue((Integer)metrics.getMetricValue(subscriptionsMessagesPerInterval) == 23);//23 = 88-65
    	assertTrue((Integer)metrics.getMetricValue(apiSubscriptionsMessagesPerInterval) == 12);//12 = 47-35
    	assertTrue((Integer)metrics.getMetricValue(adminSubscriptionsMessagesPerInterval) == 3);//3 = 18-15
    	assertTrue((Integer)metrics.getMetricValue(proxySubscriptionsMessagesPerInterval) == 8);//8=23-15
    	
    	
    }
    
    
}

