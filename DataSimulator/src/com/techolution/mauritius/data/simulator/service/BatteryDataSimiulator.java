package com.techolution.mauritius.data.simulator.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONObject;

import com.techolution.mauritus.data.Telemetry;

public class BatteryDataSimiulator implements IStubData {

	Logger log=Logger.getLogger(BatteryDataSimiulator.class.getName());
	@Override
	public void startProcess(int meterId, String startTime, String endTime, long sleepTime, int incrementtime) {
		// TODO Auto-generated method stub
		
		System.out.println("meterid:"+meterId);
		System.out.println("startTime:"+startTime);
		System.out.println("endTime:"+endTime);
		System.out.println("sleepTime:"+sleepTime);
		System.out.println("incrementtime:"+incrementtime);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
	
		Date startDate=null;
		Date endDate=null;
		if(startTime.length() <= 2){
			startDate=Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
			
		}else{
			try {
				startDate=myFormat.parse(startTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(endTime.length()<=2){
			Calendar enddateCal=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			enddateCal.add(Calendar.DATE,1);
			endDate=enddateCal.getTime();
			
		}else{
			try {
				endDate=myFormat.parse(endTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		int i = 0;
		long batteryVal=3200;
		while(startDate.before( endDate)){
			
			
			try {
				Telemetry telemetry=new Telemetry();
				telemetry.setDate(startDate);
				
				if( i == 10){
					batteryVal = batteryVal -ThreadLocalRandom.current().nextLong(10, 30);
					i = 0;
				}
				
				telemetry.setBattery(batteryVal);
				
				if(batteryVal < 2000){
					batteryVal = 3000;
				}
				
				telemetry.setMeter_id(meterId);
				sendBattery(telemetry,startDate);
				
				if(sleepTime > 100){
					Thread.sleep(sleepTime);
				}
				Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				calendar.setTime(startDate);
				calendar.add(Calendar.MILLISECOND,incrementtime);
				startDate=calendar.getTime();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
		}

	}
	
	
	
	private void sendBattery(Telemetry telemetry,Date date){
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dateVal=myFormat.format(date);
		JSONObject json = new JSONObject();
		json.put("battery", telemetry.getBattery());    
		json.put("date", dateVal);

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
		    HttpPost request = new HttpPost("http://localhost:8085/insert/telemetry/data/"+telemetry.getMeter_id());
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

}
