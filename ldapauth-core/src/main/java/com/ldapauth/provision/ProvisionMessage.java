package com.ldapauth.provision;

public class ProvisionMessage {

	String id;
    String topic;
    String actionType;
    String sendTime;
    Object content;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public ProvisionMessage() {
    }

	public ProvisionMessage(String id,String topic, String actionType, String sendTime,  Object content) {
		super();
		this.id = id;
		this.topic = topic;
		this.actionType = actionType;
		this.sendTime = sendTime;
		this.content = content;
	}


}
