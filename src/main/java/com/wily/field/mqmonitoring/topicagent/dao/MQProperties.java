package com.wily.field.mqmonitoring.topicagent.dao;

/**
 * Container class for login data
 * 
 * @author Andreas Reiss - CA Wily Professional Service
 *
 */
public class MQProperties {

    private final int port;
    private final String host;
    private final String channel;
    private final String queueManager;
    private String user;
    private String password;
    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPasswordSet() {
        return passwordSet;
    }

    private boolean passwordSet = false;

    public MQProperties(String host, int port, String channel, String queueManager) {
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.queueManager = queueManager;
    }

    public MQProperties(String host, int port, String channel, String queueManager, String user,
        String password) {
        this(host, port, channel, queueManager);
        if (user != null && password != null) {
            passwordSet = true;
            this.user = user;
            this.password = password;
        }
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    public Object getChannel() {
        return channel;
    }

    public String getQueueManager() {
        return queueManager;
    }

}