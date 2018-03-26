package com.techolution.mauritius.smartwater.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.domain.MeterConnection;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedConsumption;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedDeviceStatus;
import com.techolution.mauritius.smartwater.repository.ConnectionDetailsRepository;

@Component
public class ConsolidatedDataService {
	private Log log = LogFactory.getLog(ConsolidatedDataService.class);
	
	private static String INFLUX_CONNECTION_STRING="http://52.170.92.62:8086";
	private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root"; 
	
	private static String dbName = "mauritius_smartwater";
	private static String TAG_METER_ID = "meter_id";
	private static String WORKING = "WORKING";
	private static String NOT_WORKING = "NOT WORKING";
	
	@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;
	
	
	
	public List<MeterConnection> getAllConnections(){
		
		log.info("Entering ConsolidatedDataService.getAllConnections ");
		List<MeterConnection> returnList= (List<MeterConnection>)connectionDetailsRepository.findAll();
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
	
	public TotalConsolidatedConsumption getConsumptionForThisMonth() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.set(Calendar.DAY_OF_MONTH, 1);
		calendarlastmonth.add(Calendar.MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select sum(value) from flowvalues where time >='"+reformattedStr+"'";
		
		String query_previousbucket="select sum(value) from flowvalues where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'" ;
		
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		Double consumption = getConsumption(queryResult);
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, dbName));
		Double previousconsumption = getConsumption(queryResultPreviousBucket);
		log.debug("consumption:"+consumption);
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(consumption, 100.00, 0.00, previousconsumption);
		return consolidatedConsumption;
		
	}
	
	
public TotalConsolidatedConsumption getConsumptionForToday() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance();
		//calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.add(Calendar.DAY_OF_MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select sum(value) from flowvalues where time >='"+reformattedStr+"'";
		
		String query_previousbucket="select sum(value) from flowvalues where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'" ;
		
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		Double consumption = getConsumption(queryResult);
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, dbName));
		Double previousconsumption = getConsumption(queryResultPreviousBucket);
		log.debug("consumption:"+consumption);
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(consumption, 100.00, 0.00, previousconsumption);
		return consolidatedConsumption;
		
	}



	
	public TotalConsolidatedDeviceStatus getDeviceStatusForThisMonth() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.set(Calendar.DAY_OF_MONTH, 1);
		calendarlastmonth.add(Calendar.MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select last(value) from devicestatus where time >='"+reformattedStr+"' group by meter_id";
		
		String query_previousbucket="select last(value) from devicestatus where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'  group by meter_id" ;
		
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		List<Result> results=queryResult.getResults();
		
		int totalDevices=results.size();
		int workingdevicescount =0;
		int notworkingdevicescount = 0;
		
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
				}else{
					notworkingdevicescount++;
				}
			}
		}
		
		
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, dbName));
		
		
		int notworkingdevicescountpreviousbucket = 0;
		
		for(Result result:queryResultPreviousBucket.getResults()){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				Integer value=(Integer)(values.get(0).get(1));
				if(value.intValue() == 0){
					notworkingdevicescountpreviousbucket++;
				}
			}
		}
		
		
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedDeviceStatus consolidatedDeviceStatus=new TotalConsolidatedDeviceStatus(totalDevices, workingdevicescount,notworkingdevicescount, notworkingdevicescountpreviousbucket);
		return consolidatedDeviceStatus;
		
	}

	
  public TotalConsolidatedDeviceStatus getDeviceStatusForToday() throws ClientProtocolException, IOException, JSONException{
		
		
		Calendar calendar=Calendar.getInstance();
		
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		reformattedStr = myFormat.format(calendar.getTime());
		
		Calendar calendarlastmonth=Calendar.getInstance();
		calendarlastmonth.add(Calendar.DAY_OF_MONTH, -1);
		String reformattedStrlastmonth = myFormat.format(calendarlastmonth.getTime());
		
		//String query=INFLUX_ENDPOINT+"select sum(value) from flow where time >='"+reformattedStr+"'";
		String query="select last(value) from devicestatus where time >='"+reformattedStr+"' group by meter_id";
		
		String query_previousbucket="select last(value) from devicestatus where time >='"+reformattedStrlastmonth+"' and time < '"+reformattedStr+"'  group by meter_id" ;
		
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		List<Result> results=queryResult.getResults();
		
		int totalDevices=results.size();
		int workingdevicescount =0;
		int notworkingdevicescount = 0;
		
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
				}else{
					notworkingdevicescount++;
				}
			}
		}
		
		
		QueryResult queryResultPreviousBucket = influxDB.query(new Query(query_previousbucket, dbName));
		
		
		int notworkingdevicescountpreviousbucket = 0;
		
		for(Result result:queryResultPreviousBucket.getResults()){
			
			List<Series> seriesvalues=result.getSeries();
			if(seriesvalues == null)
				break;
			for(Series series:seriesvalues){
				List<List<Object>> values=series.getValues();
				Integer value=(Integer)(values.get(0).get(1));
				if(value.intValue() == 0){
					notworkingdevicescountpreviousbucket++;
				}
			}
		}
		
		
		
		//TotalConsolidatedConsumption consolidatedConsumption=new TotalConsolidatedConsumption(Long.valueOf(consumption).longValue(), 100.00, 0.00, 0.00);
		TotalConsolidatedDeviceStatus consolidatedDeviceStatus=new TotalConsolidatedDeviceStatus(totalDevices, workingdevicescount,notworkingdevicescount, notworkingdevicescountpreviousbucket);
		return consolidatedDeviceStatus;
		
	}
	
	private Double getConsumption(QueryResult queryResult) {
		List<Result> results=queryResult.getResults();
		Result valueobj=results.get(0);
		if(valueobj != null && valueobj.getSeries()!=null && valueobj.getSeries().size()>0){
			List<List<Object>> values=valueobj.getSeries().get(0).getValues();
//			String consumption=(String)(values.get(0).get(1));
			Double consumption=(Double)(values.get(0).get(1));
			return consumption;	
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
		
		
		
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
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
