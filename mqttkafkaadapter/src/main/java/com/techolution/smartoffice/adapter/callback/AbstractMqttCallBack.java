package com.techolution.smartoffice.adapter.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import com.techolution.smartoffice.adapter.CustomProperties;




public abstract class AbstractMqttCallBack implements MqttCallback {
	
	private Log log = LogFactory.getLog(AbstractMqttCallBack.class);

	@Autowired
	CustomProperties customProperties;
	
	protected MqttAsyncClient mqtt;
	
	
	@Autowired
    protected KafkaTemplate<String, String> template;
    
	
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		try {
			connect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

		public void subscribe() throws MqttException {
		//int[] qos = new int[mqttTopicFilters.length];
		/*for (int i = 0; i < qos.length; ++i) {
			qos[i] = 0;
		}*/
		if(mqtt == null)
		{
			this.connect();
		}
		if(!mqtt.isConnected()){
			mqtt.connect();
		}
		mqtt.subscribe(customProperties.getMqtttopic(), 0);
	}
	
	
	public void connect() throws MqttException {
		log.debug("Broker is"+customProperties.getMqttbroker());
		if(customProperties!=null){
			mqtt = new MqttAsyncClient(customProperties.getMqttbroker(),customProperties.getMqttclientid());
			mqtt.setCallback(this);	
			IMqttToken token=mqtt.connect();
			//log.debug("Connection to "+customProperties.getMqttbroker()+" completed successfully");
			mqtt.setCallback(this);
			while(!token.isComplete()){
			//	log.debug("Waiting for connection to complete");
			}
			mqtt.subscribe(customProperties.getMqtttopic(), 0);
			
			//token.
		//	log.info("MQTT CLIENT HAS BEEN CONNECTED");
		}else{
			log.warn("Properties is  null");
		}
		
		
		
	}

	/*public SmartOfficeMqttCallBack() {
		super();
		try {
			this.connect();
			this.subscribe();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
