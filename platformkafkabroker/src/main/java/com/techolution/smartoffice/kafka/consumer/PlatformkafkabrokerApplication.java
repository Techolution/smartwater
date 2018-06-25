package com.techolution.smartoffice.kafka.consumer;



import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import com.techolution.smartoffice.kafka.consumer.data.KeyValue;
import com.techolution.smartoffice.kafka.consumer.data.SeriesPointData;

@SpringBootApplication
public class PlatformkafkabrokerApplication implements CommandLineRunner  {
	
	
	
	@Autowired
    CustomProperties customProperties;

    public static Logger logger = LoggerFactory.getLogger(PlatformkafkabrokerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PlatformkafkabrokerApplication.class, args);
	}
	
	 @KafkaListener(topics = "smartoffice")
	    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
	        //logger.info(cr.toString());
	        String msgvalue=(String)cr.value();
	        logger.info("Value is:"+msgvalue);
	        
	        JSONObject object=new JSONObject(msgvalue);
	        
	        SeriesPointData seriesPointData=new SeriesPointData();
	        seriesPointData.setName("roomambience");
			
			KeyValue tag=new KeyValue();
			tag.setKey("roomid");
			tag.setValue((String)object.get("room_id"));
			
			List<KeyValue> tagList=new ArrayList<KeyValue>();
			tagList.add(tag);
			
			
			KeyValue temperature=new KeyValue();
			temperature.setKey("temperature");
		//	BigDecimal temp = new BigDecimal(object.getDouble("Temperature"));
			//Float tempval=new Float(object.getDouble("Temperature"));
			temperature.setValue(object.getDouble("Temperature"));
			
			KeyValue humidity=new KeyValue();
			humidity.setKey("humidity");
			//BigDecimal humid = new BigDecimal(object.getDouble("humidity"));
		//	Float humidval=new Float(Math.ceobject.getDouble("humidity"));
		//	humidval.
			humidity.setValue(object.getDouble("humidity"));
			
			
			List<KeyValue> valuelist=new ArrayList<KeyValue>();
			valuelist.add(temperature);
			valuelist.add(humidity);
			
			sendData("roomambience", tagList, valuelist);
	       // latch.countDown();
	    }
	 
	 @KafkaListener(topics = "bmstest")
	    public void listenBMS(ConsumerRecord<?, ?> cr) throws Exception {
	        //logger.info(cr.toString());
	        String msgvalue=(String)cr.value();
	        logger.info("Value is:"+msgvalue);
	        
	        JSONObject object=new JSONObject(msgvalue);
	        
	        SeriesPointData seriesPointData=new SeriesPointData();
	        seriesPointData.setName("batteryhealth");
			
			KeyValue tag=new KeyValue();
			tag.setKey("battery_id");
			tag.setValue((String)object.get("battery_id"));
			
			List<KeyValue> tagList=new ArrayList<KeyValue>();
			tagList.add(tag);
			
			
			KeyValue temperature=new KeyValue();
			temperature.setKey("voltage");
		//	BigDecimal temp = new BigDecimal(object.getDouble("Temperature"));
			//Float tempval=new Float(object.getDouble("Temperature"));
			temperature.setValue(object.getDouble("voltage"));
			
			KeyValue humidity=new KeyValue();
			humidity.setKey("current");
			//BigDecimal humid = new BigDecimal(object.getDouble("humidity"));
		//	Float humidval=new Float(Math.ceobject.getDouble("humidity"));
		//	humidval.
			humidity.setValue(object.getDouble("current"));
			
			
			List<KeyValue> valuelist=new ArrayList<KeyValue>();
			valuelist.add(temperature);
			valuelist.add(humidity);
			
			sendData("batteryhealth", tagList, valuelist);
	       // latch.countDown();
	    }
	 
	 
	 protected void sendData(String name,List<KeyValue> tags,List<KeyValue> values) throws JSONException{
			
			JSONObject json = new JSONObject();
			
			
			json.put("name", name);    
		
			
			
			json.put("tags", tags);
			json.put("values", values);

			System.out.println("JSON is:"+json.toString());
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			try {
			    HttpPost request = new HttpPost(customProperties.getServiceurl());
			    StringEntity params = new StringEntity(json.toString());
			    request.addHeader("content-type", "application/json");
			    request.setEntity(params);
			    httpClient.execute(request);
			// handle response here...
			} catch (Exception ex) {
			    // handle exception here
			} finally {
			    try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
