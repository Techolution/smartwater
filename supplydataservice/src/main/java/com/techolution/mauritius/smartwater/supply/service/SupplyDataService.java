package com.techolution.mauritius.smartwater.supply.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.supply.InfluxProperties;
import com.techolution.mauritius.smartwater.supply.domain.DailyWaterSupplyData;
import com.techolution.mauritius.smartwater.supply.domain.MeterConnection;
import com.techolution.mauritius.smartwater.supply.domain.SupplyStatisticsRequestData;
import com.techolution.mauritius.smartwater.supply.domain.TotalConsolidatedConsumption;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyData;

@Component
public class SupplyDataService {
	
	
	
	private static String SERIES_OFF_DATA="supplyoffdata";
	private static String SERIES_ON_DATA="supplyondata";
	
	private static double UNIT_RATE=10.0;
	
	@Autowired
    InfluxProperties influxProperties;
	
	@Autowired
    InfluxDBUtils influxDBUtils;
	
	@Autowired
	SupplyAnalyticsService supplyAnalyticService;
	
	private Log log = LogFactory.getLog(SupplyDataService.class);
	public WaterSupplyData getLatestWaterSupplyData(int meterId) throws ClientProtocolException, IOException,  URISyntaxException{
		
		log.info("Entering SupplyDataService.getLatestWaterSupplyData");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String query="select last(value) from supplyondata,supplyoffdata where meter_id='"+meterId+"'";
		log.debug("Query:"+query);
		long startStarttime=System.currentTimeMillis();
		String startTime;
		String endTime;
		Date startDate=null;
		Date endDate=null;
		try {
			JSONObject jsonObject=influxDBUtils.executeQuery(query);
			log.debug("jsonObject:"+jsonObject.toString());
			long endtime=System.currentTimeMillis();
			//log.debug("Time After getDailyMetrics query execution:"+endtime);
			log.debug("Time Taken for getLatestWaterSupplyData query execution:"+(endtime-startStarttime));
			
			JSONArray  array=jsonObject.getJSONArray("results");
			log.debug("Array Length is:"+array.length());
			JSONObject jsonObject2=array.getJSONObject(0);
			log.debug("jsonObject2:"+jsonObject2.toString());
			
			JSONArray seriesArray=jsonObject2.getJSONArray("series");
			int length=seriesArray.length();
			log.debug("Series Length is:"+length);
			
			startTime = null;
			endTime = null;
			
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
						endDate=date;
					}else{
						startDate=date;
						startTime=myFormat.format(date);
					}
					
				}
				
				
				
			}
		} catch (JSONException e) {
			log.error("JSON Exception occured. So setting default values");
			Calendar startCal=Calendar.getInstance();
			Calendar endCal=Calendar.getInstance();
			
			startCal.set(Calendar.HOUR_OF_DAY,6);
			endCal.set(Calendar.HOUR_OF_DAY,18);
			
			startTime=myFormat.format(startCal.getTime());
			endTime=myFormat.format(endCal.getTime());
		}
		
		Map <Long, MeterConnection> connectionmap= supplyAnalyticService.getConnectionsMap();
		Date currentTime=Calendar.getInstance().getTime();
		
		String STATUS= "ON";
		if(endDate != null && endDate.before(currentTime) &&  startDate!=null && startDate.before(currentTime) && endDate.after(startDate) ){
			
			STATUS= "OFF";
		}
		
		double currentMonthConsumption=0.0;
		double lastMonthConsumption= 0.0;
		try {
			TotalConsolidatedConsumption consumptionStats=getConsumptionForThisMonth(meterId);
			currentMonthConsumption=consumptionStats.getTotalConsumption();
			lastMonthConsumption=consumptionStats.getConsumptionInPreviousBucket();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Calendar today=Calendar.getInstance();
		int daysPastInMonth=today.get(Calendar.DAY_OF_MONTH);
		double estimatedCurrentMonthConsumption=currentMonthConsumption;
		if(daysPastInMonth<30){
			double averagePerDay=currentMonthConsumption/daysPastInMonth;
			
			 estimatedCurrentMonthConsumption=currentMonthConsumption+(averagePerDay*(30-daysPastInMonth));
	
		}
				
		int factor=1;
		
		if(estimatedCurrentMonthConsumption<lastMonthConsumption){
			factor=-1;
		}
		
		double percent=Math.abs((estimatedCurrentMonthConsumption-lastMonthConsumption)/lastMonthConsumption);
		double effectivePercent=1+((factor)*percent);
		
		double estimatedConsumptionForNextMonth=effectivePercent*estimatedCurrentMonthConsumption;
		
		double revenueLastMonth=Math.round((lastMonthConsumption*UNIT_RATE)*100D)/100D;
		double expectedRevenueThisMonth=Math.round((estimatedCurrentMonthConsumption*UNIT_RATE)*100D)/100D;
		double projectedRevenueNextMonth=Math.round((estimatedConsumptionForNextMonth*UNIT_RATE)*100D)/100D;
		double revenueTillDate=Math.round((currentMonthConsumption*UNIT_RATE)*100D)/100D;
		
		System.out.println(connectionmap);
		MeterConnection connection=connectionmap.get(new Long(meterId));
		WaterSupplyData waterSupplyData=new WaterSupplyData();
		waterSupplyData.setLastOffTime(endTime);
		waterSupplyData.setLastOnTime(startTime);
		waterSupplyData.setMeterId(new Long(meterId).longValue());
		waterSupplyData.setLocation(connection.getHouse_namenum());
		waterSupplyData.setCustomerId(connection.getCustomer_id());
		waterSupplyData.setMetertype(connection.getMetertype());
		waterSupplyData.setCurrentstatus(STATUS);
		waterSupplyData.setImage(connection.getImage());
		waterSupplyData.setPipesize(connection.getPipesize());
		waterSupplyData.setPipesizeunit(connection.getPipesizeunit());
		waterSupplyData.setCurrentMonthRevenueTillDate(revenueTillDate);
		waterSupplyData.setCurrentMonthConsumptionTillDate(Math.round(currentMonthConsumption*100D)/100D);
		waterSupplyData.setLastMonthConsumption(Math.round(lastMonthConsumption*100D)/100D);
		waterSupplyData.setProjectedCurrentMonthConsumption(Math.round(estimatedCurrentMonthConsumption*100D)/100D);
		waterSupplyData.setProjectedNextMonthConsumption(Math.round(estimatedConsumptionForNextMonth*100D)/100D);
		
		waterSupplyData.setRevenueLastMonth(revenueLastMonth);
		waterSupplyData.setProjectedRevenueNextMonth(projectedRevenueNextMonth);
		waterSupplyData.setProjectedRevenueThisMonth(expectedRevenueThisMonth);
		
		log.info("Exiting SupplyDataService.getLatestWaterSupplyData");
		return waterSupplyData;
	}
	
	
	public List<DailyWaterSupplyData> getDataForList(SupplyStatisticsRequestData data){
		
		
		log.info("Entering SupplyDataService.getDataForList");
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String startTime=myFormat.format(data.getStartDate());
		String endTime=myFormat.format(data.getEndDate());
		Calendar currentCal=Calendar.getInstance();
		Date currentTime=currentCal.getTime();
		if(data.getEndDate().after(currentTime)){
			
			currentCal.add(Calendar.DATE, -1);
			endTime=myFormat.format(currentCal.getTime());
		}
		
		String query = "select time,value from "+SERIES_ON_DATA+" , "+SERIES_OFF_DATA+ " where meter_id='"+data.getMeterId()+"' and time >= '"+startTime+"' and time <='"+endTime+"' order by time asc";
		
		
		
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
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
	
	public TotalConsolidatedConsumption getConsumptionForThisMonth( int meterid) throws ClientProtocolException, IOException, JSONException{
		
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar todaysDate=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		todaysDate.add(Calendar.DATE,1);
		Date todaysDateVal=todaysDate.getTime();
		String todaysDateStr=myFormat.format(todaysDateVal);
		
		
		
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.set(Calendar.DAY_OF_MONTH, 1);
		calendarlastmonth.add(Calendar.MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select sum(value) from flowvalues where time >='"+reformattedStr+"' and time <'"+todaysDateStr+"' and meter_id='"+meterid+"'";
		
		log.debug("Query is :"+query);
		
		String query_previousbucket="select sum(value) from flowvalues where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"' and meter_id='"+meterid+"'" ;
		log.debug("Query for last month is :"+query_previousbucket);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		Double consumption = getConsumption(queryResult);
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, influxProperties.getDbname()));
		Double previousconsumption = getConsumption(queryResultPreviousBucket);
		log.debug("consumption:"+consumption);
		
		influxDB.close();
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(consumption, 100.00, 0.00, previousconsumption);
		return consolidatedConsumption;
		
	}

private Double getConsumption(QueryResult queryResult) {
	List<Result> results=queryResult.getResults();
	Result valueobj=results.get(0);
	if(valueobj != null && valueobj.getSeries()!=null && valueobj.getSeries().size()>0){
		List<List<Object>> values=valueobj.getSeries().get(0).getValues();
//		String consumption=(String)(values.get(0).get(1));
		Double consumption=(Double)(values.get(0).get(1));
		return Math.round(consumption*100D)/100D;	
	}else{
		return new Double(0.0);
	}
	
}

}
