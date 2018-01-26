package com.wily.field.mqmonitoring.topicagent.entity;


import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Adam Bezecny - CA Services
 *
 * This class represents list of topics. Internally Topic objects are stored in HashMap
 * with TopicString instance used as key. This is to enable fast retrieval of objects by topic string
 *
 */
public class Topics {

	private HashMap<TopicString, Topic> topics;

	public Topics() {
		topics = new HashMap<TopicString, Topic>();
	}

	
	public void addTopic(Topic topic){
		this.topics.put(topic.getTopicString(), topic);
	}
	
	public Map<TopicString, Topic> getTopics() {
		return topics;
	}

	/**
	 * 
	 * @param topicString
	 * @return
	 */
	public Topic getTopicByTopicString(String topicString){
		return topics.get(new TopicString(topicString));
	}

	@Override
	public String toString() {
		return "Topics [topics=" + topics + "]";
	}

	
}
