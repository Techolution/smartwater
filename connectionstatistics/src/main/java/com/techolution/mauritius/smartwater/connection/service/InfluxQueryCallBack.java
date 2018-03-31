package com.techolution.mauritius.smartwater.connection.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.connection.domain.BatteryData;
import com.techolution.mauritius.smartwater.connection.domain.Data;

@Component
public class InfluxQueryCallBack {
	
	private Log log = LogFactory.getLog(InfluxQueryCallBack.class);

	
	public void processBatteryAPIResult(int deviceId, String locationName, List<Result> resultlist, List<Data> retlist) {
		//		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			//dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			//Date date1=new SimpleDateFormat("yyyy-MM-DDTHH:mm:ssz").parse(sDate1);
			BatteryData resultData=null;
			
		//	Instant  instant=null;
			for(Result result:resultlist){
				List<Series> serieslist=result.getSeries();
				if(serieslist == null){
					break;
				}
				for(Series series:serieslist){
					List<List<Object>> valuelist=series.getValues();
					for(List<Object> results:valuelist){
						String endTimeReturned=(String)results.get(0);
						//log.debug("Date is:"+(endTimeReturned.split("T"))[0]);
					//	instant= Instant.parse( endTimeReturned); 
				//		Date date=java.util.Date.from(instant);
					//	Date date=dateFormat.parse(endTimeReturned);
						if(results.get(1)!=null){
							
					//	Date date=dateFormat.parse(endTimeReturned.split("T")[0]);
						resultData=new BatteryData();
						resultData.setDevid(deviceId);
						resultData.setName(endTimeReturned.split("T")[0]);	
						double currentPower=((Double)results.get(1)).doubleValue();
						double currentPercent=(currentPower/ConnectionStatisticsService.TOTALPOWER)*100;
						
						if(currentPercent >= 80){
							resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_GOOD);
						}else if (currentPercent >=60){
							resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_MODERATE);
						}else if(currentPercent >=30){
							resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_WEAK);
						}else{
							resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_POOR);
						}
						
						double currentCapacity=(currentPercent/100)*ConnectionStatisticsService.TOTALCAPACITY;
						resultData.setCurrentCapacity(new Double(currentCapacity).intValue());
						resultData.setCurrentHealthPercentage(new Double(currentPercent).intValue());
						resultData.setCurrentPower(new Double(currentPower).intValue());
						resultData.setTotalPower(ConnectionStatisticsService.TOTALPOWER);
						resultData.setTotalCapacity(ConnectionStatisticsService.TOTALCAPACITY);
						resultData.setTemperature(ConnectionStatisticsService.TEMPERATURE);
						
						resultData.setValue(currentPower);
						resultData.setSensor_locationname(locationName);
						retlist.add(resultData);
						}
					}
					
					
				}
				
			}
	}
	
	
	public List<Data> proccessBatteryUsingHTTPCall(int deviceId, String locationName, JSONObject responsejson)
			throws JSONException {
		List<Data> retlist;
		JSONArray resultsArray=responsejson.getJSONArray("results");
		JSONObject object=(JSONObject)resultsArray.get(0);
		JSONArray seriesArray=object.getJSONArray("series");
		log.debug("seriesArray length is:"+seriesArray.length());
		JSONObject seriesobject=(JSONObject)seriesArray.get(0);
		JSONArray valuesArray=seriesobject.getJSONArray("values");
		log.debug("valuesArray length is:"+valuesArray.length());
		int valueslength=valuesArray.length();
		BatteryData resultData=null;
		retlist=new ArrayList<Data>();
		for(int index=0;index<valueslength;index++){
			JSONArray valueobj=valuesArray.getJSONArray(index);
			
			resultData=new BatteryData();
			resultData.setDevid(deviceId);
			resultData.setName(((String)valueobj.get(0)).split("T")[0]);
			Integer currentPowerVal=(Integer)valueobj.get(1);
			
			double currentPower=currentPowerVal.doubleValue();
			double currentPercent=(currentPower/ConnectionStatisticsService.TOTALPOWER)*100;
			
			if(currentPercent >= 80){
				resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_GOOD);
			}else if (currentPercent >=60){
				resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_MODERATE);
			}else if(currentPercent >=30){
				resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_WEAK);
			}else{
				resultData.setHealthStatus(ConnectionStatisticsService.HEALTH_POOR);
			}
			
			double currentCapacity=(currentPercent/100)*ConnectionStatisticsService.TOTALCAPACITY;
			resultData.setCurrentCapacity(new Double(currentCapacity).intValue());
			resultData.setCurrentHealthPercentage(new Double(currentPercent).intValue());
			resultData.setCurrentPower(new Double(currentPower).intValue());
			resultData.setTotalPower(ConnectionStatisticsService.TOTALPOWER);
			resultData.setTotalCapacity(ConnectionStatisticsService.TOTALCAPACITY);
			resultData.setTemperature(ConnectionStatisticsService.TEMPERATURE);
			
			
			resultData.setValue(((Integer)valueobj.get(1)).doubleValue());
			resultData.setSensor_locationname(locationName);
			retlist.add(resultData);
			
		}
		return retlist;
	}


}
