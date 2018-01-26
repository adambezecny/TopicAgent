package com.wily.field.mqmonitoring.topicagent.entity;

/**
 * 
 * @author Adam Bezecny - CA Services
 * 
 * Represents type of subscription. 
 * ALL represents all 3 types (i.e. ALL = API + PROXY + ADMIN) 
 *
 */
public enum SubscriptionType {
	ALL,
	API,
	PROXY,
	ADMIN
}
