package com.techolution.mauritius.smartwater.supply.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.supply.InfluxProperties;
import com.techolution.mauritius.smartwater.supply.domain.ConsumptionLeakage;
import com.techolution.mauritius.smartwater.supply.domain.LeakageData;
import com.techolution.mauritius.smartwater.supply.domain.MeterConnection;
import com.techolution.mauritius.smartwater.supply.domain.MeterTrendData;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyDailyConnectionStats;
import com.techolution.mauritius.smartwater.supply.repository.ConnectionDetailsRepository;


@Component
public class SupplyAnalyticsService {
	
	@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;
	
	
	@Autowired
    InfluxProperties influxProperties;
	
	private static long MONTHLY_THRESHOLD=50000;
	private static long DAILY_THRESHOLD=4000;
	private static long DAILY_LOWER_THRESHOLD=1000;
	
	private static String METER_ID= "meter_id";
	
	
	private static String EQUALTO_QUOTE="='";
	private static String QUOTE="'";
	private static String SPACE=" ";
	private static String OR="OR";
	
	private static String UP="UP";
	private static String DOWN="DOWN";
	private static String DEFAULT_LOCATION="TEST";
	
	private static String SERIES_NAME_CONSUMERLEAKAGE="consumerleakage";
	private static String SERIES_NAME_NETWORKLEAKAGE="networkleakage";
	
	
	
	private Log log = LogFactory.getLog(SupplyAnalyticsService.class);
	
	private static Map <Long, MeterConnection> connectionmap=null;
	
	public List<MeterTrendData> getConnectionsAboveDailyBaseline(){
		
		log.info("Entering SupplyAnalyticsService.getConnectionsAboveDailyBaseline");
		
		List<MeterTrendData> retList=new ArrayList<MeterTrendData>();
		
		
		getConnectionsMap();
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startDate=Calendar.getInstance();
		
		Calendar endDate=Calendar.getInstance();
		endDate.add(Calendar.DATE,1);
		
		String startTime=myFormat.format(startDate.getTime());
		String endTime=myFormat.format(endDate.getTime());
		
		retList=getMeterConnectionForDates( startTime, endTime,null,DAILY_THRESHOLD,">");
		
		if(retList.size()>0){
			
		
			StringBuffer wherclause = createWhereClause(retList);
			
			
			
			log.debug("Where clause constructed is:"+wherclause.toString());
			
			Calendar previousDay=Calendar.getInstance();
			previousDay.add(Calendar.DATE, -1);
			
			String prevstat=myFormat.format(previousDay.getTime());
			
			List<MeterTrendData> yestedayData=getMeterConnectionForDates( prevstat, startTime,wherclause.toString(),DAILY_THRESHOLD,">");
			
			populatePreviousucketAndConnectionStatisticsInCurrent(retList, yestedayData);
		
		}
		
		
		log.info("Existing SupplyAnalyticsService.getConnectionsAboveDailyBaseline");
		return retList;
		
		
	}


	private StringBuffer createWhereClause(List<MeterTrendData> retList) {
		int index=0;
		StringBuffer wherclause=new StringBuffer();
		for(MeterTrendData data:retList){
			
			if(index !=0){
				wherclause.append(OR);
			}
			wherclause.append(SPACE);
			wherclause.append(METER_ID);
			wherclause.append(EQUALTO_QUOTE);
			wherclause.append(data.getMeterId());
			wherclause.append(QUOTE);
			wherclause.append(SPACE);
			index++;
		}
		return wherclause;
	}
	
	
  public List<MeterTrendData> getConnectionsBelowDailyBaseline(){
		
		log.info("Entering SupplyAnalyticsService.getConnectionsBelowDailyBaseline");
		
		List<MeterTrendData> retList=new ArrayList<MeterTrendData>();
		
		getConnectionsMap();
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startDate=Calendar.getInstance();
		
		Calendar endDate=Calendar.getInstance();
		endDate.add(Calendar.DATE,1);
		
		String startTime=myFormat.format(startDate.getTime());
		String endTime=myFormat.format(endDate.getTime());
		
		retList=getMeterConnectionForDates( startTime, endTime,null,DAILY_LOWER_THRESHOLD,"<");
		
		if(retList.size()>0){
			
		
			StringBuffer wherclause = createWhereClause(retList);
			
			
			
			log.debug("Where clause constructed is:"+wherclause.toString());
			
			Calendar previousDay=Calendar.getInstance();
			previousDay.add(Calendar.DATE, -1);
			
			String prevstat=myFormat.format(previousDay.getTime());
			
			List<MeterTrendData> yestedayData=getMeterConnectionForDates( prevstat, startTime,wherclause.toString(),DAILY_LOWER_THRESHOLD,"<");
			
			populatePreviousucketAndConnectionStatisticsInCurrent(retList, yestedayData);
		
		}
		
		
		log.info("Existing SupplyAnalyticsService.getConnectionsBelowDailyBaseline");
		return retList;
		
		
	}


public Map <Long, MeterConnection> getConnectionsMap() {
	if(SupplyAnalyticsService.connectionmap == null){
		SupplyAnalyticsService.connectionmap=getAllConnections();
	}
	return SupplyAnalyticsService.connectionmap;
}

	private void populatePreviousucketAndConnectionStatisticsInCurrent(List<MeterTrendData> retList,
			List<MeterTrendData> yestedayData) {
		Map <Integer, MeterTrendData> previousbucketmap = yestedayData.stream().collect(Collectors.toMap(conn -> conn.getMeterId(),conn -> conn));
		
		retList.forEach(meterdata -> {
			
			MeterTrendData previousBucketData=previousbucketmap.get(meterdata.getMeterId());
			if(previousBucketData!=null){
				meterdata.setPreviousConsumption(previousBucketData.getConsumption());
				
				if(meterdata.getConsumption() < previousBucketData.getConsumption()){
					meterdata.setComparisonResult(DOWN);
				}else{
					meterdata.setComparisonResult(UP);
				}	
			}else{
				meterdata.setComparisonResult(UP);
			}
			
			
			MeterConnection connection=connectionmap.get(meterdata.getMeterId());
			
			if(connection!=null){
				meterdata.setLocation(connection.getHouse_namenum());
			}else{
				meterdata.setLocation(DEFAULT_LOCATION);
			}
			
		});
	}

	private List<MeterTrendData> getMeterConnectionForDates( String startTime, String endTime,String meterIdAndClause,long threshold,String compareCondition) {
		
		
		String query = "select * from (select sum(value) as dailyconsumtpion from flowvalues where time >= '"+startTime+"' and time <'"+endTime+"' group by meter_id) where dailyconsumtpion "+compareCondition+SPACE+ threshold;
		
		if(meterIdAndClause !=null){
			query = "select * from (select sum(value) as dailyconsumtpion from flowvalues where time >= '"+startTime+"' and time <'"+endTime+"'"+"AND ("+meterIdAndClause+")"+" group by meter_id)";
		}
		
		log.debug("Query is:"+query);
		
		List<MeterTrendData> retList=new ArrayList<MeterTrendData>();
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		long endtime=System.currentTimeMillis();
		log.debug("Time After getConnectionsAboveDailyBaseline query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		
		List<Result> resultList=queryResult.getResults();
		
		if (resultList == null || resultList .isEmpty()){
			return null;
		}else{
			resultList.forEach(result ->{
				List<Series> seriesResult=result.getSeries();
				
				if(seriesResult != null && !seriesResult.isEmpty()){
					
					seriesResult.forEach(series -> {
						
						List<List<Object>> values=series.getValues();
						
						if(values !=null && values.size()>0){
							values.forEach(value -> {
								MeterTrendData data=new MeterTrendData();
								data.setMeterId(Integer.parseInt((String)value.get(2)));
								data.setConsumption((((Double)value.get(1))).longValue());
								data.setDateTime(startTime);
								data.setAnalysisStartTime(startTime);
								data.setAnalysisEndTime(endTime);
								retList.add(data);
							});
							
						}
						
					});
					
				}
				
			});
			
		}
		influxDB.close();
		
		return retList;
	}
	
	
  public Map <Long, MeterConnection> getAllConnections(){
		
		log.info("Entering ConsolidatedDataService.getAllConnections ");
		List<MeterConnection> returnList= (List<MeterConnection>)connectionDetailsRepository.findAll();
		log.info("Exiting ConsolidatedDataService.getAllConnections ");
		if(returnList == null) {
			log.debug("returnList size is null");
		}else{
			log.debug("List size is:"+returnList.size());	
		}
		
		Map <Long, MeterConnection> map = returnList.stream().collect(Collectors.toMap(conn -> conn.getHouse_id(),conn -> conn));
		
		return map;
	}
  
  
  public WaterSupplyDailyConnectionStats getStats(){
		
		log.info("Entering SupplyAnalyticsService.getStats");
		
		WaterSupplyDailyConnectionStats connectionStats=new WaterSupplyDailyConnectionStats();
		getConnectionsMap();
		int totalMeters=SupplyAnalyticsService.connectionmap.size();
		connectionStats.setTotalMeters(totalMeters);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar start=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String startTime=myFormat.format(start.getTime());
		
		start.add(Calendar.DATE,1);
		String endTime=myFormat.format(start.getTime());
		
		String countOfSupply="select count(*) from supplyondata where time >='"+startTime+"' and time <'"+endTime+"' group by meter_id";
		
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(countOfSupply, influxProperties.getDbname()));
		long endtime=System.currentTimeMillis();
		log.debug("Time After countOfSupply query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		
		log.debug("Count of supply query is:"+countOfSupply);
		
		List<Result> results=queryResult.getResults();
		
		int nummetersupplied=0;
		
		if(results != null && !results.isEmpty()){
			
			Result result=results.get(0);
			
			List<Series> serieslist=result.getSeries();
			//log.debug("supply Series list size is:"+serieslist.size());
			if(serieslist !=null && !serieslist.isEmpty())
			{
				nummetersupplied=serieslist.size();
					/*for(Series series:serieslist)
					{
					
						List<List<Object>> res=series.getValues();
						
						if(res !=null && !res.isEmpty()){
						
							res.forEach( val -> System.out.println("Value is:"+val));
							nummetersupplied=res.size();
						}
				}*/
			}
		}
		log.debug("totalMeters:"+totalMeters);
		log.debug("nummetersupplied:"+nummetersupplied);
		
		int nosupply=totalMeters-nummetersupplied;
		if(nosupply < 0){
			nosupply=0;
		}
		connectionStats.setNoSupplyCount(nosupply);
		
		int noResponseDevices =0;
		
		String noResponseQuery="select meter_id,last(value) from devicestatus group by meter_id";
		
		QueryResult queryResult2 = influxDB.query(new Query(noResponseQuery, influxProperties.getDbname()));
		
		List<Result> results2=queryResult2.getResults();
		
		
		if(results2 != null && !results2.isEmpty()){
			
			Result result=results2.get(0);
			
			List<Series> serieslist=result.getSeries();
			
			if(serieslist !=null && !serieslist.isEmpty()){
				
				log.debug("Series list size is:"+serieslist.size());
				for(Series series:serieslist)
				{
				
					List<List<Object>> res=series.getValues();
					
					if(res !=null && !res.isEmpty()){
						
						for(List<Object> list:res){
							
				/*			log.debug("Meterid:"+list.get(1));
							log.debug("value:"+list.get(2));*/
							Double statusval=(Double)list.get(2);
							if(statusval == -1){
								noResponseDevices++;
							}
							
						}
					}
				}
			}
		}
		
		
		connectionStats.setNoResponseCount(noResponseDevices);
		
		
		influxDB.close();
		log.info("Exiting SupplyAnalyticsService.getStats");
		return connectionStats;
	}
  
  public LeakageData  getCurrentDayLeakageData(){
	  
		  log.info("Entering SupplyAnalyticsService.getStats");
		  
		  SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			
		  Calendar start=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		  String startTime=myFormat.format(start.getTime());
			
		  start.add(Calendar.DATE,1);
		  String endTime=myFormat.format(start.getTime());
		  
		  String leakageval="select sum(value) from networkleakage,consumerleakage where time >='"+startTime+"' and time <'"+endTime+"' ";
		  
		  
		  InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
	      long startStarttime=System.currentTimeMillis();
		  QueryResult queryResult = influxDB.query(new Query(leakageval, influxProperties.getDbname()));
		  long endtime=System.currentTimeMillis();
		  log.debug("Time After countOfSupply query execution:"+endtime);
		  log.debug("Time Taken for query execution:"+(endtime-startStarttime));
			
		  LeakageData leakageData=new LeakageData();
		 // leakageData.setMetricsDate((Calendar.getInstance(influxProperties.getDatatimezone())));
			
		 List<Result> results=queryResult.getResults();
		 if(results!=null && results.size()>0){
			 Result result=results.get(0);
			 List<Series> serieslist=result.getSeries();
			 serieslist.forEach(series -> {
				 String seriesName= series.getName();
				 List<List<Object>> values=series.getValues();
				 if(values !=null && values.size()>0){
					 List<Object> value=values.get(0);
					 if(SERIES_NAME_CONSUMERLEAKAGE.equalsIgnoreCase(seriesName)){
						 leakageData.setConsumptionLeakage((Double)value.get(1));
						 String time=(String)value.get(0);
						 Instant instant=Instant.parse(time);
						 
						 leakageData.setMetricsDate(Date.from(instant));
					 }else{
						 leakageData.setNetworkLeakage((Double)value.get(1));
						 String time=(String)value.get(0);
						 Instant instant=Instant.parse(time);
						 
						 leakageData.setMetricsDate(Date.from(instant));
					 }
				 }
				 
			 });
		 }
		  
		  
		 log.info("Exiting SupplyAnalyticsService.getStats");
		 return leakageData;
	  
  }
  
  
  public List<ConsumptionLeakage>  getCurrentDayConsumerLeakage(){
	  
	  log.info("Entering SupplyAnalyticsService.getCurrentDayConsumerLeakage");
	  
	  SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		
	  Calendar start=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
	  String startTime=myFormat.format(start.getTime());
		
	  start.add(Calendar.DATE,1);
	  String endTime=myFormat.format(start.getTime());
	  
	  String leakageval="select sum(value) from consumerleakage where time >='"+startTime+"' and time <'"+endTime+"' group by meter_id ";
	  
	  
	  InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
      long startStarttime=System.currentTimeMillis();
	  QueryResult queryResult = influxDB.query(new Query(leakageval, influxProperties.getDbname()));
	  long endtime=System.currentTimeMillis();
	  log.debug("Time After countOfSupply query execution:"+endtime);
	  log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		
	  
	  List<ConsumptionLeakage> resultList=new ArrayList<ConsumptionLeakage>();
	
	 // leakageData.setMetricsDate((Calendar.getInstance(influxProperties.getDatatimezone())));
		
	 List<Result> results=queryResult.getResults();
	 if(results!=null && results.size()>0){
		 Result result=results.get(0);
		 List<Series> serieslist=result.getSeries();
		 serieslist.forEach(series -> {
			 ConsumptionLeakage leakage= new ConsumptionLeakage();
			
			 String meterId=series.getTags().get(METER_ID);
			 leakage.setMeterId(meterId);
			 List<List<Object>> values=series.getValues();
			 if(values !=null && values.size()>0){
				 	List<Object> value=values.get(0);
				 	leakage.setLeakage( (Double)value.get(1));
					 
					 String time=(String)value.get(0);
					 Instant instant=Instant.parse(time);
					 leakage.setDate(Date.from(instant));
					 
					 
				}
			 resultList.add(leakage);
			 
		 });
	 }
	  
	  
	 log.info("Exiting SupplyAnalyticsService.getCurrentDayConsumerLeakage");
	 return resultList;
  
}

}
