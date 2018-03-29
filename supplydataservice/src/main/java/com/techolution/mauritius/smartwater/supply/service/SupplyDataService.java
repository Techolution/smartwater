package com.techolution.mauritius.smartwater.supply.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.supply.controller.SupplyDataController;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyData;

@Component
public class SupplyDataService {
	
	
	private static String INFLUX_CONNECTION_STRING="http://52.170.92.62:8086";
	private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root";
	private static String SERIES_OFF_DATA="supplyoffdata";
	private static String SERIES_ON_DATA="supplyondata";
	
	private Log log = LogFactory.getLog(SupplyDataService.class);
	public WaterSupplyData getLatestWaterSupplyData(int meterId) throws ClientProtocolException, IOException, JSONException, URISyntaxException{
		
		log.info("Entering SupplyDataService.getLatestWaterSupplyData");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String query="select last(value) from supplyondata,supplyoffdata where meter_id='"+meterId+"'";
		
		JSONObject jsonObject=InfluxDBUtils.executeQuery(query);
		
		JSONArray  array=jsonObject.getJSONArray("results");
		log.debug("Array Length is:"+array.length());
		JSONObject jsonObject2=array.getJSONObject(0);
		
		JSONArray seriesArray=jsonObject2.getJSONArray("series");
		int length=seriesArray.length();
		log.debug("Series Length is:"+length);
		
		String startTime=null;
		String endTime=null;
		for(int index=0;index<length;index++){
			
			JSONObject object=(JSONObject)seriesArray.get(index);
			String name=object.getString("name");
			JSONArray values=object.getJSONArray("values");
			if(values.length()>0){
				JSONArray value=values.getJSONArray(0);
				String dateTime=(String)value.get(0);
				Instant instant=Instant.parse(dateTime);
				Date date=Date.from(instant);
				if(SERIES_OFF_DATA.equalsIgnoreCase(name)){
					endTime=myFormat.format(date);
				}else{
					startTime=myFormat.format(date);
				}
				
			}
			
			
			
		}
		
		WaterSupplyData waterSupplyData=new WaterSupplyData();
		waterSupplyData.setLastOffTime(endTime);
		waterSupplyData.setLastOnTime(startTime);
		
		
		log.info("Exiting SupplyDataService.getLatestWaterSupplyData");
		return waterSupplyData;
	}

}
