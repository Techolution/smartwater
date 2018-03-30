package com.techolution.mauritius.smartwater.supply.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
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

import com.techolution.mauritius.smartwater.supply.domain.MeterConnection;
import com.techolution.mauritius.smartwater.supply.domain.MeterTrendData;
import com.techolution.mauritius.smartwater.supply.repository.ConnectionDetailsRepository;


@Component
public class SupplyAnalyticsService {
	
	@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;
	
	private static long MONTHLY_THRESHOLD=50000;
	private static long DAILY_THRESHOLD=4000;
	private static long DAILY_LOWER_THRESHOLD=1000;
	
	private static String METER_ID= "meter_id";
	
	private static String INFLUX_CONNECTION_STRING="http://52.170.92.62:8086";
	private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root";
	private static String EQUALTO_QUOTE="='";
	private static String QUOTE="'";
	private static String SPACE=" ";
	private static String OR="OR";
	
	private static String UP="UP";
	private static String DOWN="DOWN";
	private static String DEFAULT_LOCATION="TEST";
	
	
	private static String dbName = "mauritius_smartwater";
	
	private Log log = LogFactory.getLog(SupplyAnalyticsService.class);
	
	private static Map <Long, MeterConnection> connectionmap=null;
	
	public List<MeterTrendData> getConnectionsAboveDailyBaseline(){
		
		log.info("Entering SupplyAnalyticsService.getConnectionsAboveDailyBaseline");
		
		List<MeterTrendData> retList=new ArrayList<MeterTrendData>();
		
		if(SupplyAnalyticsService.connectionmap == null){
			SupplyAnalyticsService.connectionmap=getAllConnections();
		}
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
		
		if(SupplyAnalyticsService.connectionmap == null){
			SupplyAnalyticsService.connectionmap=getAllConnections();
		}
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
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
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

}
