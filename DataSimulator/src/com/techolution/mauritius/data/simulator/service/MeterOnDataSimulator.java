package com.techolution.mauritius.data.simulator.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.techolution.mauritus.data.KeyValue;
import com.techolution.mauritus.data.Telemetry;

public class MeterOnDataSimulator extends InsertSeriesData implements IStubData {

	Logger log=Logger.getLogger(MeterOnDataSimulator.class.getName());
	@Override
	public void startProcess(int meterId, String startTime, String endTime, long sleepTime, int incrementtime) {
		// TODO Auto-generated method stub
		
		System.out.println("meterid:"+meterId);
		System.out.println("startTime:"+startTime);
		System.out.println("endTime:"+endTime);
		System.out.println("sleepTime:"+sleepTime);
		System.out.println("incrementtime:"+incrementtime);
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("Indian/Mauritius"));
		
	
		Date startDate=null;
		Date endDate=null;
		if(startTime.length() <= 2){
			startDate=Calendar.getInstance(TimeZone.getTimeZone("Indian/Mauritius")).getTime();
			
		}else{
			try {
				startDate=myFormat.parse(startTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(endTime.length()<=2){
			Calendar enddateCal=Calendar.getInstance(TimeZone.getTimeZone("Indian/Mauritius"));
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
		
		
		
		
		while(startDate.before( endDate)){
			
			
			try {
				Telemetry telemetry=new Telemetry();
				telemetry.setDate(startDate);
				
				KeyValue tag=new KeyValue();
				tag.setKey("meter_id");
				tag.setValue(meterId);
				
				List<KeyValue> tagList=new ArrayList<KeyValue>();
				tagList.add(tag);
				
				
				KeyValue value=new KeyValue();
				value.setKey("value");
				value.setValue(1);
				
				List<KeyValue> valuelist=new ArrayList<KeyValue>();
				valuelist.add(value);
				
				this.sendData("meteron", startDate, tagList, valuelist);
				
				if(sleepTime > 100){
					Thread.sleep(sleepTime);
				}
				Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("Indian/Mauritius"));
				calendar.setTime(startDate);
				int rand=new Long(ThreadLocalRandom.current().nextLong(-90000, 900000)).intValue();
				calendar.add(Calendar.MILLISECOND,(incrementtime+rand));
				startDate=calendar.getTime();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}

	}
	
	
	
	
}
