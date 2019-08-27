package com.techolution.mauritius.smartwater.supply.service;


import java.net.UnknownHostException;
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
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
	private static String SERIES_NAME_CONSUMERLEAKAGE_ON="consumerleakageontime";
	private static String SERIES_NAME_CONSUMERLEAKAGE_OFF="consumerleakageofftime";
	
	
	
	private Log log = LogFactory.getLog(SupplyAnalyticsService.class);
	
	
	/*@Autowired
	RedisProperties redisProperties;
	
	@Autowired
    private RedisAutoConfiguration redisAutoConfiguration;*/

	public List<MeterTrendData> getConnectionsAboveDailyBaseline() throws UnknownHostException{
		
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
		
		Map <Long, MeterConnection> connMap=getConnectionsMap();
		
		retList.forEach(trendData -> {
			
			MeterConnection connection=connMap.get(new Long(trendData.getMeterId()));
			if(connection!=null){
				trendData.setLocation(connection.getHouse_namenum());
			}
		});
		
		
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
	
	
  public List<MeterTrendData> getConnectionsBelowDailyBaseline() throws UnknownHostException{
		
		log.info("Entering SupplyAnalyticsService.getConnectionsBelowDailyBaseline");
		
		List<MeterTrendData> retList=new ArrayList<MeterTrendData>();
		
		//getConnectionsMap();
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
		
		Map <Long, MeterConnection> connMap=getConnectionsMap();
		
		retList.forEach(trendData -> {
			
			MeterConnection connection=connMap.get(new Long(trendData.getMeterId()));
			if(connection!=null){
				trendData.setLocation(connection.getHouse_namenum());
			}
		});
		
		log.info("Existing SupplyAnalyticsService.getConnectionsBelowDailyBaseline");
		return retList;
		
		
	}


public Map <Long, MeterConnection> getConnectionsMap() throws UnknownHostException {
	
	return getAllConnections();
}

	private void populatePreviousucketAndConnectionStatisticsInCurrent(List<MeterTrendData> retList,
			List<MeterTrendData> yestedayData) throws UnknownHostException {
		Map <Integer, MeterTrendData> previousbucketmap = yestedayData.stream().collect(Collectors.toMap(conn -> conn.getMeterId(),conn -> conn));
		
		final Map<Long, MeterConnection> connectionMap =  getAllConnections();
		
		
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
			
			
			MeterConnection connection =null;
			
			connection = connectionMap.get(meterdata.getMeterId());
			
			
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
	
	
	/*@Bean
	 JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory connectionFactory=new JedisConnectionFactory();
		connectionFactory.setHostName(redisProperties.getHost());
		connectionFactory.setPort(redisProperties.getPort());
	  return connectionFactory;
	 }*/
	
  public Map <Long, MeterConnection> getAllConnections() throws UnknownHostException{
		
		log.info("Entering SupplyAnalyticsService.getAllConnections ");
		Map <Long, MeterConnection> map = null;
		
		//RedisTemplate<Object,Object> template=redisAutoConfiguration.redisTemplate(jedisConnectionFactory());
		RedisTemplate<Object,Object> template= null;
		
		if(template ==null || template.opsForValue().get("ALL_CONNECTIONS")==null){
		
			log.info("Data not present in redis. Populating it");
			List<MeterConnection> returnList= (List<MeterConnection>)connectionDetailsRepository.findAll();
			log.info("Exiting ConsolidatedDataService.getAllConnections ");
			if(returnList == null) {
				log.debug("returnList size is null");
			}else{
				log.debug("List size is:"+returnList.size());	
			}
			
			 map = returnList.stream().collect(Collectors.toMap(conn -> conn.getHouse_id(),conn -> conn));	
			 //template.opsForValue().set("ALL_CONNECTIONS", map);
			
			
			
		}else{
			
			log.info("Data IS present in redis for all connections");
			map=(Map <Long, MeterConnection>)template.opsForValue().get("ALL_CONNECTIONS");
		}
		
		
		return map;
	}
  
  
  public WaterSupplyDailyConnectionStats getStats() throws UnknownHostException{
		
		log.info("Entering SupplyAnalyticsService.getStats");
		
		WaterSupplyDailyConnectionStats connectionStats=new WaterSupplyDailyConnectionStats();
		//getConnectionsMap();
		int totalMeters=this.getAllConnections().size();
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
		  
		//  String leakageval="select sum(value) from networkleakage,consumerleakage where time >='"+startTime+"' and time <'"+endTime+"' ";
		  //TODO Have proper date specific logic once leakage is properly setup and developed
		  String leakageval="select sum(value) from networkleakage,consumerleakage  ";
		  
		  
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
		  
		 influxDB.close();
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
				 	leakage.setLeakageVolume( (Double)value.get(1));
					 
					 String time=(String)value.get(0);
					 Instant instant=Instant.parse(time);
					 leakage.setDate(Date.from(instant));
					 
					 
				}
			 resultList.add(leakage);
			 
		 });
	 }
	 influxDB.close();
	  
	 log.info("Exiting SupplyAnalyticsService.getCurrentDayConsumerLeakage");
	 return resultList;
  
}
  
  public List<ConsumptionLeakage>  getCurrentDayConsumerLeakageDetails() throws UnknownHostException{
	  
	  log.info("Entering SupplyAnalyticsService.getCurrentDayConsumerLeakageDetails");
	  
	 // SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
	  List<ConsumptionLeakage>  outputlist= new ArrayList<ConsumptionLeakage>();
		
	  //TODO Add date specific filtering again
		/*String startTime=myFormat.format(data.getStartDate());
		String endTime=myFormat.format(data.getEndDate());
		Calendar currentCal=Calendar.getInstance();
		Date currentTime=currentCal.getTime();
		if(data.getEndDate().after(currentTime)){
			
			currentCal.add(Calendar.DATE, -1);
			endTime=myFormat.format(currentCal.getTime());
		}*/
		
	//	String query = "select time,value from "+SERIES_NAME_CONSUMERLEAKAGE_ON+" , "+SERIES_NAME_CONSUMERLEAKAGE_OFF+ " where meter_id='"+data.getMeterId()+"' and time >= '"+startTime+"' and time <='"+endTime+"' order by time asc";
		String query = "select * from "+SERIES_NAME_CONSUMERLEAKAGE_ON+" , "+SERIES_NAME_CONSUMERLEAKAGE_OFF+" order by time asc";
		
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		long endtime=System.currentTimeMillis();
		log.debug("Time After getCurrentDayConsumerLeakageDetails query execution:"+endtime);
		log.debug("Time Taken for getCurrentDayConsumerLeakageDetails query execution:"+(endtime-startStarttime));
		log.debug("Query is:"+query);
		
		List<Result> resultlist=queryResult.getResults();
		
		Map<String,ConsumptionLeakage> outputmap=new HashMap<String,ConsumptionLeakage>();
		if(resultlist != null && resultlist.size()>0){
			
			Result result=resultlist.get(0);
			List<Series> serieslist=result.getSeries();
			serieslist.forEach(series -> {
				
				String name=series.getName();
				
				List<List<Object>> valuelist=series.getValues();
				valuelist.parallelStream().forEach( values -> {
					
					String timestamp=(String)values.get(0);
					String meter_id=(String)values.get(1);
					
					Instant instant=Instant.parse(timestamp);
					Date dt=Date.from(instant);
					
										
					
					ConsumptionLeakage leakageData=outputmap.get(meter_id);
					if(leakageData == null){
						leakageData =new ConsumptionLeakage();
					}
					leakageData.setMeterId(meter_id);
					
					if(SERIES_NAME_CONSUMERLEAKAGE_ON.equalsIgnoreCase(name)){
						leakageData.setStartTime(dateFormat.format(dt));
					}else{
						leakageData.setEndTime(dateFormat.format(dt));
					}
					
					outputmap.put(meter_id, leakageData);
					
					
				});
			});
			
		}
		
	
		//TODO Add time base filter. It also assumes that data will be here whenever data is there in consumerleakageontime and consumerleakageofftime
		String leakagevalquery="select sum(value) from consumerleakage group by meter_id ";
		long leakagevalqueryresultStarttime=System.currentTimeMillis();
		QueryResult leakagevalqueryresult = influxDB.query(new Query(leakagevalquery, influxProperties.getDbname()));
		long leakagevalqueryresultendtime=System.currentTimeMillis();
		log.debug("Time Taken for leakagevalquery query execution:"+(leakagevalqueryresultendtime-leakagevalqueryresultStarttime));
		List<Result> leakageValResult=leakagevalqueryresult.getResults();
		if(leakageValResult != null && leakageValResult.size()>0){
			Result result=leakageValResult.get(0);
			
			List<Series> serieslist=result.getSeries();
			
			serieslist.forEach(series -> {
				String meterId=series.getTags().get("meter_id");
				
				List<List<Object>> valuelist=series.getValues();
				if(valuelist !=null && valuelist.size()>0){
					List<Object> value=valuelist.get(0);
					ConsumptionLeakage leakage=outputmap.get(meterId);
					leakage.setLeakageVolume((Double)value.get(1));
				}
				
			});
		}
		
		
		/*Collection<DailyWaterSupplyData> ouput=outputmap.values();
		List<DailyWaterSupplyData> outputlist=new ArrayList<DailyWaterSupplyData>();
		outputlist.addAll(ouput);*/
	  
		Map <Long, MeterConnection> connmap=getConnectionsMap();
		Collection<ConsumptionLeakage> ouput=outputmap.values();
		outputlist.addAll(ouput);
		outputlist.forEach(consumptionleakage -> {
			Long meterId=new Long(consumptionleakage.getMeterId());
			MeterConnection meterconn= connmap.get(meterId);
			if(meterconn!=null){
				consumptionleakage.setLocation(meterconn.getHouse_namenum());
			}
		});
		
		influxDB.close();
		log.info("Exiting SupplyAnalyticsService.getCurrentDayConsumerLeakageDetails");
		return outputlist;
  
}

}
