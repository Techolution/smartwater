package com.techolution.smartoffice.kafka.consumer;



import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;

import com.techolution.smartoffice.kafka.consumer.data.KeyValue;
import com.techolution.smartoffice.kafka.consumer.data.SeriesPointData;

@SpringBootApplication
public class PlatformkafkabrokerApplication implements CommandLineRunner  {
	
	
	
	@Autowired
    CustomProperties customProperties;
	
	@Autowired
	Environment environment;

    public static Logger logger = LoggerFactory.getLogger(PlatformkafkabrokerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PlatformkafkabrokerApplication.class, args);
	}
	
	 @KafkaListener(topics = "smartoffice" )
	    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
	        //logger.info(cr.toString());
		 
		  String[] profiles=environment.getActiveProfiles();
		  
		  if(!profiles[0].equalsIgnoreCase("default")){
			  return;
		  }
		  
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
		 String[] profiles=environment.getActiveProfiles();
		  
		  if(!profiles[0].contains("bms")){
			  return;
		  }
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
			
			
			List<KeyValue> valuelist=new ArrayList<KeyValue>();
			KeyValue temperature=new KeyValue();
			temperature.setKey("voltage");
		//	BigDecimal temp = new BigDecimal(object.getDouble("Temperature"));
			//Float tempval=new Float(object.getDouble("Temperature"));
			temperature.setValue(object.getDouble("voltage")+0.000001);
			
			KeyValue humidity=new KeyValue();
			humidity.setKey("current");
			//BigDecimal humid = new BigDecimal(object.getDouble("humidity"));
		//	Float humidval=new Float(Math.ceobject.getDouble("humidity"));
		//	humidval.
			humidity.setValue(object.getDouble("current")+0.00000000000000000001);
			
			if(object.has("SOC")){
				
			
				KeyValue soc=new KeyValue();
				soc.setKey("SOC");
				soc.setValue(object.getDouble("SOC")+0.000000001);
				valuelist.add(soc);
			}
			
			if(object.has("SOH")){
				KeyValue soh=new KeyValue();
				soh.setKey("SOH");
				soh.setValue(object.getDouble("SOH")+0.000000001);
				valuelist.add(soh);
				
			}
			
			
			if(object.has("power")){
				KeyValue power=new KeyValue();
				power.setKey("power");
				power.setValue(object.getDouble("power"));
				valuelist.add(power);
				
			}
			
			valuelist.add(temperature);
			valuelist.add(humidity);
			
			sendData("batteryhealth", tagList, valuelist);
	       // latch.countDown();
	    }
	 
	 
	 
	// @KafkaListener(topics = "transmon")
	    public void listenCESC(ConsumerRecord<?, ?> cr) throws Exception {
	        logger.info(cr.toString());
		 String[] profiles=environment.getActiveProfiles();
		  
		  if(!profiles[0].contains("cesc")){
			  return;
		  }
		  DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	        String msgvalue=(String)cr.value();
	        logger.info("Value is:"+msgvalue);
	        
	        JSONObject object=new JSONObject(msgvalue);
	        
	        JSONObject sensorData=object.getJSONObject("sensorData");
	        
	        JSONObject vibrationData=object.getJSONObject("vibData");
	        
	        String timestamp=object.getString("timeStamp");
	        //Instant instant=Instant.parse(timestamp);
	        Date date = formatter.parse(timestamp.split("\\+")[0]);
	        
	        
	        
	        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String dateVal=myFormat.format(date);
			logger.info("dateVal is:"+dateVal);
	        
	        SeriesPointData seriesPointData=new SeriesPointData();
	        
	        seriesPointData.setTimestamp(date);
	        seriesPointData.setName("transformer");
			
			KeyValue tag=new KeyValue();
			tag.setKey("devID");
			tag.setValue((String)object.get("devID"));
			
			
			KeyValue tag2=new KeyValue();
			tag2.setKey("transID");
			tag2.setValue((String)object.get("transID"));
			
			
			List<KeyValue> tagList=new ArrayList<KeyValue>();
			tagList.add(tag);
			tagList.add(tag2);
			
			
			List<KeyValue> valuelist=new ArrayList<KeyValue>();
			
			KeyValue dataType=new KeyValue();
			dataType.setKey("data_type");
		//	BigDecimal temp = new BigDecimal(object.getDouble("Temperature"));
			//Float tempval=new Float(object.getDouble("Temperature"));
			dataType.setValue(object.getString("dataType"));
			valuelist.add(dataType);
			
			KeyValue oiltemperature=null;
			if(sensorData.has("oilTemp")){
			
				oiltemperature=new KeyValue();
				oiltemperature.setKey("oil_temperature");
				oiltemperature.setValue(Float.valueOf(sensorData.getString("oilTemp")));
				valuelist.add(oiltemperature);
			}
			KeyValue moisture=null;
			if(sensorData.has("moisture")){
			
				moisture=new KeyValue();
				moisture.setKey("moisture");
				moisture.setValue(Float.valueOf(sensorData.getString("moisture")));
				valuelist.add(moisture);
			}
			
			KeyValue lt1=null;
			KeyValue lt2=null;
			KeyValue lt3=null;
			
			if(sensorData.has("LT")){
				
				JSONArray array=sensorData.getJSONArray("LT");
				lt1=new KeyValue();
				lt1.setKey("lt_temperature_1");
				lt1.setValue(Float.valueOf(array.getString(0)));
				
				
				lt2=new KeyValue();
				lt2.setKey("lt_temperature_2");
				lt2.setValue(Float.valueOf(array.getString(1)));
				
				
				lt3=new KeyValue();
				lt3.setKey("lt_temperature_3");
				lt3.setValue(Float.valueOf(array.getString(2)));
				
				valuelist.add(lt1);
				valuelist.add(lt2);
				valuelist.add(lt3);
				
				
			}
			
			if(vibrationData != null){
				
				KeyValue gavlue=null;
				KeyValue freq_1=null;
				KeyValue freq_2=null;
				KeyValue freq_3=null;
				
				if(vibrationData.has("gValue")){
					gavlue=new KeyValue();
					gavlue.setKey("g_value");
					gavlue.setValue(Float.valueOf(vibrationData.getString("gValue")));
					valuelist.add(gavlue);
					JSONArray array=vibrationData.getJSONArray("freq");
					
					freq_1=new KeyValue();
					freq_1.setKey("vibration_primiary_freq");
					freq_1.setValue(Float.valueOf(array.getString(0)));
					
					freq_2=new KeyValue();
					freq_2.setKey("vibration_secondry_freq");
					freq_2.setValue(Float.valueOf(array.getString(1)));
					
					freq_3=new KeyValue();
					freq_3.setKey("vibration_tertiary_freq");
					freq_3.setValue(Float.valueOf(array.getString(2)));
					valuelist.add(gavlue);
					valuelist.add(freq_1);
					valuelist.add(freq_2);
					valuelist.add(freq_3);
				}
				
			}
			
						
			sendData("transformer", tagList, valuelist,dateVal);
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
	 protected void sendData(String name,List<KeyValue> tags,List<KeyValue> values,String timeStamp) throws JSONException{
			
			JSONObject json = new JSONObject();
			
			
			json.put("name", name);    
			json.put("timestamp", timeStamp);
			
			
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
	 
	 @KafkaListener(topics = "aademo" )
	    public void listenAssetLocation(ConsumerRecord<?, ?> cr) throws Exception {
	        //logger.info(cr.toString());
		 
		  String[] profiles=environment.getActiveProfiles();
		  
		 
		  SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String dateVal=myFormat.format(Calendar.getInstance().getTime());
			logger.info("dateVal is:"+dateVal);
			
	        String msgvalue=(String)cr.value();
	        logger.info("Value is:"+msgvalue);
	        
	        JSONObject object=new JSONObject(msgvalue);
	        
	        SeriesPointData seriesPointData=new SeriesPointData();
	        seriesPointData.setName("aaassetslocation");
			
			KeyValue tag=new KeyValue();
			tag.setKey("assetId");
			tag.setValue((String)object.get("assetid"));
			
			List<KeyValue> tagList=new ArrayList<KeyValue>();
			tagList.add(tag);
			
			
			KeyValue latitidue=new KeyValue();
			latitidue.setKey("latitude");
		//	BigDecimal temp = new BigDecimal(object.getDouble("Temperature"));
			//Float tempval=new Float(object.getDouble("Temperature"));
			latitidue.setValue(object.getDouble("latitude"));
			
			KeyValue longitude=new KeyValue();
			longitude.setKey("longitude");
			//BigDecimal humid = new BigDecimal(object.getDouble("humidity"));
		//	Float humidval=new Float(Math.ceobject.getDouble("humidity"));
		//	humidval.
			longitude.setValue(object.getDouble("longitude"));
			
			KeyValue distancemoved=new KeyValue();
			distancemoved.setKey("distancemoved");
			//BigDecimal humid = new BigDecimal(object.getDouble("humidity"));
		//	Float humidval=new Float(Math.ceobject.getDouble("humidity"));
		//	humidval.
			distancemoved.setValue(object.getDouble("distancemoved"));
			
			
			List<KeyValue> valuelist=new ArrayList<KeyValue>();
			tagList.add(latitidue);
			tagList.add(longitude);
			valuelist.add(distancemoved);
			
			sendData("aaassetslocation", tagList, valuelist,dateVal);
	       // latch.countDown();
	    }

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
