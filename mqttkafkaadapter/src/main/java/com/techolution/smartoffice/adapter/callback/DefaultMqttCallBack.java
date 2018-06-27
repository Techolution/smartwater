package com.techolution.smartoffice.adapter.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
public class DefaultMqttCallBack extends AbstractMqttCallBack {

	private Log log = LogFactory.getLog(DefaultMqttCallBack.class);
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		
		
	
		byte[] payload = message.getPayload();
		String messageReceived=new String(payload);
		log.debug("Before sending message:"+messageReceived);
		//log.debug("Before sending message:"+object.toString());
		this.template.send(this.customProperties.getKafkatopic(),messageReceived);
		
	}

}
