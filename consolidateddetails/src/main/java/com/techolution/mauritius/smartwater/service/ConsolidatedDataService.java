package com.techolution.mauritius.smartwater.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.InfluxProperties;
import com.techolution.mauritius.smartwater.domain.MeterConnection;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedConsumption;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedDeviceStatus;
import com.techolution.mauritius.smartwater.repository.ConnectionDetailsRepository;

@Component
public class ConsolidatedDataService {
	private Log log = LogFactory.getLog(ConsolidatedDataService.class);
	
	/*private static String influxProperties.getUrl()="http://34.66.131.198:8086";
	private static String influxProperties.getUsername()="root";
	private static StringinfluxProperties.getPassword()="root"; 
	
	private static String influxProperties.getDbname() = "mauritius_smartwater";*/
	private static String TAG_METER_ID = "meter_id";
	private static String WORKING = "WORKING";
	private static String NOT_WORKING = "NOT WORKING";
	
	@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;
	
	@Autowired
    InfluxProperties influxProperties;
	//TODO CHANGE TO PROPER CACHING
	//private static List<MeterConnection> connections=null;
	//private 
	
	/*@Autowired
	RedisProperties redisProperties;*/
	
	/*@Autowired
    private RedisAutoConfiguration redisAutoConfiguration;*/
	
	/*@Bean
	 JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory connectionFactory=new JedisConnectionFactory();
		connectionFactory.setHostName(redisProperties.getHost());
		connectionFactory.setPort(redisProperties.getPort());
	  return connectionFactory;
	 }*/
	
	public List<MeterConnection> getAllConnections() throws UnknownHostException{
		
		log.info("Entering ConsolidatedDataService.getAllConnections ");
		List< MeterConnection> connections = null;
		
		connections = getFromRedis();
		
		List<MeterConnection> returnList= connections;
		log.info("Exiting ConsolidatedDataService.getAllConnections ");
		if(returnList == null) {
			log.debug("returnList size is null");
		}else{
			log.debug("List size is:"+returnList.size());	
		}
		try {
			Map<String,String> currentStatusList=getLatestDeviceStatusForAllDevices();
			if (currentStatusList !=null){
				returnList.parallelStream().forEach(meterconnection -> meterconnection.setCurrentstatus(currentStatusList.getOrDefault(Long.toString(meterconnection.getHouse_id())
						,WORKING))
							);
				
				}
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnList;
	}

	private List<MeterConnection> getFromRedis() throws UnknownHostException {
		List<MeterConnection> connections;
		//RedisTemplate<Object,Object> template=redisAutoConfiguration.redisTemplate(jedisConnectionFactory());
		RedisTemplate<Object,Object> template=null;
		
		if(template ==null || template.opsForValue().get("ALL_CONNECTIONS_LIST")==null){
		
			log.info("Data not present in redis. Populating it");
			connections= (List<MeterConnection>)connectionDetailsRepository.findAll();
			log.info("Exiting ConsolidatedDataService.getAllConnections ");
			if(connections == null) {
				log.debug("returnList size is null");
			}else{
				log.debug("List size is:"+connections.size());	
			}
			
			
			 //template.opsForValue().set("ALL_CONNECTIONS_LIST", connections);
			
			
			
		}else{
			
			log.info("Data IS present in redis for all connections");
			connections=(List < MeterConnection>)template.opsForValue().get("ALL_CONNECTIONS_LIST");
		}
		return connections;
	}
	
	public TotalConsolidatedConsumption getConsumptionForThisMonth() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Calendar endDate=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		/*endDate.add(Calendar.DATE, 1);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 1);*/
		
		
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		String endDateStr=myFormat.format(endDate.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendarlastmonth.set(Calendar.DAY_OF_MONTH, 1);
		calendarlastmonth.add(Calendar.MONTH, -1);
		calendarlastmonth.set(Calendar.HOUR_OF_DAY, 0);
		calendarlastmonth.set(Calendar.MINUTE, 0);
		calendarlastmonth.set(Calendar.SECOND, 1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		/*endDate.add(Calendar.MONTH, -1);
		endDate.set(Calendar.DATE, 1);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 1);*/
		
		String endDateForLastMonth=myFormat.format(calendar.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select sum(value) from flowvalues where time >='"+reformattedStr+"' and time <= '"+endDateStr+"'";
		
		log.debug("Query for this month consumption of all meters:"+query);
		
		String query_previousbucket="select sum(value) from flowvalues where time >='"+reformattedStrlastmonth+"' and time < '"+endDateForLastMonth+"'" ;
		log.debug("Query for last month consumption of all meters:"+query_previousbucket);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		Double consumption = getConsumption(queryResult);
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, influxProperties.getDbname()));
		Double previousconsumption = getConsumption(queryResultPreviousBucket);
		log.debug("consumption:"+consumption);
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(consumption.longValue(), 100.00, 0.00, previousconsumption.longValue());
		return consolidatedConsumption;
		
	}
	
	
public TotalConsolidatedConsumption getConsumptionForToday() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND,0);
		//calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendarlastmonth.set(Calendar.HOUR_OF_DAY, 0);
		calendarlastmonth.set(Calendar.MINUTE, 0);
		calendarlastmonth.set(Calendar.SECOND,0);
		calendarlastmonth.add(Calendar.DAY_OF_MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select sum(value) from flowvalues where time >='"+reformattedStr+"'";
		log.debug("Query for todays consumption  is:"+query);
		
		String query_previousbucket="select sum(value) from flowvalues where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'" ;
		log.debug("Query for yesterdays consumption  is:"+query_previousbucket);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		Double consumption = getConsumption(queryResult);
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, influxProperties.getDbname()));
		Double previousconsumption = getConsumption(queryResultPreviousBucket);
		log.debug("consumption:"+consumption);
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(consumption.longValue(), 100.00, 0.00, previousconsumption.longValue());
		return consolidatedConsumption;
		
	}



	
	public TotalConsolidatedDeviceStatus getDeviceStatusForThisMonth() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Calendar endDate=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		/*endDate.add(Calendar.DATE, 1);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 1);*/
		
		
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		
		
		Calendar calendarlastmonth=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendarlastmonth.set(Calendar.DAY_OF_MONTH, 1);
		calendarlastmonth.add(Calendar.MONTH, -1);
		calendarlastmonth.set(Calendar.HOUR_OF_DAY, 0);
		calendarlastmonth.set(Calendar.MINUTE, 0);
		calendarlastmonth.set(Calendar.SECOND, 1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		/*endDate.add(Calendar.MONTH, -1);
		endDate.set(Calendar.DATE, 1);
		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 1);*/
		
		
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select last(value) from devicestatus where time >='"+reformattedStr+"' group by meter_id";
		
		
		String query_previousbucket="select last(value) from devicestatus where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'  group by meter_id" ;
		
		log.debug("Query for this month device status:"+query);
		log.debug("Query for last month device status:"+query_previousbucket);
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		List<Result> results=queryResult.getResults();
		
		int totalDevices=results.size();
		int workingdevicescount =0;
		int notworkingdevicescount = 0;
		int inactiveDeviceCount=0;
		
		for(Result result:results){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			else
				totalDevices=seriesvalues.size();
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				Double value=(Double)(values.get(0).get(1));
				if(value.intValue() == 1){
					workingdevicescount++;
				}else if(value.intValue() == -1){
					inactiveDeviceCount++;
				}else {
					notworkingdevicescount++;
				}
			}
		}
		
		
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, influxProperties.getDbname()));
		
		
		int notworkingdevicescountpreviousbucket = 0;
		
		for(Result result:queryResultPreviousBucket.getResults()){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				Double value=(Double)(values.get(0).get(1));
				if(value.intValue() == 0){
					notworkingdevicescountpreviousbucket++;
				}
			}
		}
		
		
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedDeviceStatus consolidatedDeviceStatus=new TotalConsolidatedDeviceStatus(totalDevices, workingdevicescount,notworkingdevicescount, notworkingdevicescountpreviousbucket,inactiveDeviceCount,0);
		return consolidatedDeviceStatus;
		
	}

	
  public TotalConsolidatedDeviceStatus getDeviceStatusForToday() throws ClientProtocolException, IOException, JSONException{
		
		
	  Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND,0);
		//calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		calendarlastmonth.set(Calendar.HOUR_OF_DAY, 0);
		calendarlastmonth.set(Calendar.MINUTE, 0);
		calendarlastmonth.set(Calendar.SECOND,0);
		calendarlastmonth.add(Calendar.DAY_OF_MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select last(value) from devicestatus where time >='"+reformattedStr+"' group by meter_id";
		
		String query_previousbucket="select last(value) from devicestatus where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'  group by meter_id" ;
		log.debug("Query for this todays device status:"+query);
		log.debug("Query for last yesterdays device status:"+query_previousbucket);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		List<Result> results=queryResult.getResults();
		
		int totalDevices=results.size();
		int workingdevicescount =0;
		int notworkingdevicescount = 0;
		int inactiveDevicesCount=0;
		int warningDevicesCount=0;
		
		for(Result result:results){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			else
				totalDevices=seriesvalues.size();
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				Double value=(Double)(values.get(0).get(1));
				if(value.intValue() == 1){
					workingdevicescount++;
				}else if (value.intValue() == -1){
					inactiveDevicesCount++;
				}else{
					notworkingdevicescount++;
				}
			}
		}
		
		
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, influxProperties.getDbname()));
		
		
		int notworkingdevicescountpreviousbucket = 0;
		
		for(Result result:queryResultPreviousBucket.getResults()){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				int value=((Double)(values.get(0).get(1))).intValue();
				if(value == 0){
					notworkingdevicescountpreviousbucket++;
				}
			}
		}
		
		
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedDeviceStatus consolidatedDeviceStatus=new TotalConsolidatedDeviceStatus(totalDevices, workingdevicescount,notworkingdevicescount, notworkingdevicescountpreviousbucket,inactiveDevicesCount,warningDevicesCount);
		return consolidatedDeviceStatus;
		
	}
	
	private Double getConsumption(QueryResult queryResult) {
		List<Result> results=queryResult.getResults();
		Result valueobj=results.get(0);
		if(valueobj != null && valueobj.getSeries()!=null && valueobj.getSeries().size()>0){
			List<List<Object>> values=valueobj.getSeries().get(0).getValues();
//			String consumption=(String)(values.get(0).get(1));
			Double consumption=(Double)(values.get(0).get(1));
			return Math.round(consumption*100D)/100D;	
		}else{
			return new Double(0.0);
		}
		
	}
	
  private Map<String,String> getLatestDeviceStatusForAllDevices() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance();
		
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.add(Calendar.DAY_OF_MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select last(value) from devicestatus  group by meter_id";
		
		
		
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(), influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		List<Result> results=queryResult.getResults();
		Map<String,String> statusMap=new HashMap<String,String>();
		
		
		for(Result result:results){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			else
		
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				String meterId=series.getTags().get(TAG_METER_ID);
				Double value=(Double)(values.get(0).get(1));
				if(0 == value){
				statusMap.put(meterId,NOT_WORKING);
				}else{
					statusMap.put(meterId,WORKING);
				}
		
			}
		}
		
		
		
		
		return statusMap;
		
	}
	
	
	
}
