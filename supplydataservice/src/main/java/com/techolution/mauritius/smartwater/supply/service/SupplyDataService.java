package com.techolution.mauritius.smartwater.supply.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.supply.domain.DailyWaterSupplyData;
import com.techolution.mauritius.smartwater.supply.domain.SupplyStatisticsRequestData;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyData;

@Component
public class SupplyDataService {
	
	
	private static String INFLUX_CONNECTION_STRING="http://52.170.92.62:8086";
	private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root";
	private static String SERIES_OFF_DATA="supplyoffdata";
	private static String SERIES_ON_DATA="supplyondata";
	private static String dbName = "mauritius_smartwater";
	
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
	
	
	public List<DailyWaterSupplyData> getDataForList(SupplyStatisticsRequestData data){
		
		
		log.info("Entering SupplyDataService.getDataForList");
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String startTime=myFormat.format(data.getStartDate());
		String endTime=myFormat.format(data.getEndDate());
		
		String query = "select time,value from "+SERIES_ON_DATA+" , "+SERIES_OFF_DATA+ " where meter_id='"+data.getMeterId()+"' and time >= '"+startTime+"' and time <='"+endTime+"' order by time asc";
		
		
		
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		long endtime=System.currentTimeMillis();
		log.debug("Time After getDailyMetrics query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		log.debug("Query is:"+query);
		
		List<Result> resultlist=queryResult.getResults();
		
		Map<String,DailyWaterSupplyData> outputmap=new HashMap<String,DailyWaterSupplyData>();
		if(resultlist != null && resultlist.size()>0){
			
			Result result=resultlist.get(0);
			List<Series> serieslist=result.getSeries();
			serieslist.parallelStream().forEach(series -> {
				
				String name=series.getName();
				
				List<List<Object>> valuelist=series.getValues();
				valuelist.parallelStream().forEach( values -> {
					
					String timestamp=(String)values.get(0);
					log.debug("Timestamp:"+timestamp);
					String date=timestamp.split("T")[0];
					log.debug("date:"+date);
					Instant instant=Instant.parse(timestamp);
					Date dt=Date.from(instant);
					
					Calendar cal=Calendar.getInstance();
					cal.setTime(dt);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE,0);
					cal.set(Calendar.SECOND,0);
					
					
					DailyWaterSupplyData supplydata=outputmap.get(date);
					if(supplydata == null){
						supplydata =new DailyWaterSupplyData();
					}
					
					if(SERIES_ON_DATA.equalsIgnoreCase(name)){
						supplydata.addToOnTimeList(dt);
					}else{
						supplydata.addToOffTimeList(dt);
					}
					supplydata.setDay(date);
					supplydata.setDateVal(cal.getTime());
					outputmap.put(date, supplydata);
					
					
				});
			});
			
		}
		
		Collection<DailyWaterSupplyData> ouput=outputmap.values();
		List<DailyWaterSupplyData> outputlist=new ArrayList<DailyWaterSupplyData>();
		outputlist.addAll(ouput);
		outputlist.sort((DailyWaterSupplyData d1,DailyWaterSupplyData d2) -> d1.getDateVal().compareTo(d2.getDateVal()));
		log.info("Exiting SupplyDataService.getDataForList");
		return outputlist;
	}

}
