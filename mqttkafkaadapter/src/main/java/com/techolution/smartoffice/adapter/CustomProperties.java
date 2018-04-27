package com.techolution.smartoffice.adapter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
	
	public String getMqttbroker() {
		return mqttbroker;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public void setMqttbroker(String mqttbroker) {
		this.mqttbroker = mqttbroker;
	}
	public String getMqtttopic() {
		return mqtttopic;
	}
	public void setMqtttopic(String mqtttopic) {
		this.mqtttopic = mqtttopic;
	}
	private String mqttbroker;
	private String mqtttopic;
	private String roomid;
	private String kafkatopic;
	private String mqttclientid;
	public String getMqttclientid() {
		return mqttclientid;
	}
	public void setMqttclientid(String mqttclientid) {
		this.mqttclientid = mqttclientid;
	}
	public String getKafkatopic() {
		return kafkatopic;
	}
	public void setKafkatopic(String kafkatopic) {
		this.kafkatopic = kafkatopic;
	}

	
	

}
