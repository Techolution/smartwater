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



@Component
public class SmartOfficeMqttCallBack implements MqttCallback {
	
	private Log log = LogFactory.getLog(SmartOfficeMqttCallBack.class);

	@Autowired
	CustomProperties customProperties;
	
	private MqttAsyncClient mqtt;
	
	
    @Autowired
    private KafkaTemplate<String, String> template;
	
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

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		
		//if()
		byte[] payload = arg1.getPayload();
		String messageReceived=new String(payload);
		//log.debug("RECEIVED message from mqtt:"+messageReceived);
		//TODO SInce i recived as only 41.00 doing it
		JSONObject object=new JSONObject(messageReceived);
		//TODO This is not required if the room number comes with the message
		//JSONObject object=new JSONObject();
		//object.put("room_id", customProperties.getRoomid());
		//THIS IS REQUIRED BECAUSE FEW NODES GIVES VALUES IN INTEGERS. SINCE
		//DB ALREADY HAS FLOAT, it fails
		JSONObject object2=new JSONObject();
		
		object2.put("room_id", customProperties.getRoomid());
		Double temp=object.getDouble("Temperature");
		Double humidity=object.getDouble("humidity");
		object2.put("Temperature", temp.doubleValue()+0.001);
		object2.put("humidity", humidity.doubleValue()+0.001);
	//	object.put("humidity", messageReceived);
		log.debug("Before sending message:"+object2.toString());
		//log.debug("Before sending message:"+object.toString());
		this.template.send(customProperties.getKafkatopic(),object2.toString());
		//this.template.send(customProperties.getKafkatopic(),object.toString());
		

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
		if(customProperties!=null){
			mqtt = new MqttAsyncClient(customProperties.getMqttbroker(),customProperties.getMqttclientid());
			mqtt.setCallback(this);	
			IMqttToken token=mqtt.connect();
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
