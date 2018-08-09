package com.techolution.mauritius.smartwater.connection.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.InfluxProperties;
import com.techolution.mauritius.smartwater.connection.domain.ConnectionKpiData;
import com.techolution.mauritius.smartwater.connection.domain.Data;
import com.techolution.mauritius.smartwater.connection.domain.KeyValue;
import com.techolution.mauritius.smartwater.connection.domain.Kpi;
import com.techolution.mauritius.smartwater.connection.domain.RequestData;
import com.techolution.mauritius.smartwater.connection.domain.SeriesPointData;
import com.techolution.mauritius.smartwater.connection.domain.Telemetry;
import com.techolution.mauritius.smartwater.connection.domain.TelemetryRequestData;


@Component
public class ConnectionStatisticsService {
	
	private Log log = LogFactory.getLog(ConnectionStatisticsService.class);
	
	
	
	@Autowired
    InfluxProperties influxProperties;
	
	
	@Autowired
	InfluxDBUtils influxdbutils;
	
	//TODO replace with spring properties or DB
	public  static final int TOTALCAPACITY=4200;
	public static final int TOTALPOWER=3500;
	public static double TEMPERATURE=84.2;
	
	//TODO ENum
	public static String HEALTH_GOOD="GOOD";
	public static String HEALTH_MODERATE="MODERATE";
	public static String HEALTH_WEAK="WEAK";
	public static String HEALTH_POOR="POOR";
	
	
	@Autowired
	InfluxQueryCallBack querycallback;
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	
	public List<Data> getData(RequestData data) throws ParseException{
		
		
	
		
		
		
		
		/*String startTime = myFormat.format(data.getStart_Time().getTime());
		String endTime = myFormat.format(data.getEnd_Time().getTime());*/
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		
		SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		//Date startDate=myFormat.parse(data.getStart_Time());
		String startTime = data.getStart_Time();
		//String startTime = "2018-03-01";
		//String startTime=myFormat2.format(startDate);
		
		log.debug("Start time:"+startTime);
		
		
		String endTime = data.getEnd_Time();
		
		
		endTime = getNextDay( endTime);
		/*Date endDate=myFormat.parse(endTime2);
		String endTime = myFormat2.format(endDate);
		//String endTime = "2018-03-15";
*/		
		String groupVal = getGroupVal(data);
		
		boolean useHours=false;
		
		if(data.getSample_Distance().equalsIgnoreCase("Hour")){
			useHours=true;
		}
		int deviceId=data.getHouse_ID();
		//int deviceId=123;
		String query = "select sum(value)  from flowvalues where time >='"+startTime+"' and time<'"+endTime+"' and meter_id='"+deviceId+"' group by time("+groupVal+") fill(0) TZ('"+influxProperties.getDatatimezone()+"')";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		log.debug("Query is:"+query);
		
		
		List<Data> retlist = getDailyMetrics(deviceId, query,useHours);
		return retlist;
	}


	private String getNextDay(String endTime) throws ParseException {
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		//Instant instant=Instant.parse(endTime);
		Date date=myFormat.parse(endTime);
		//Date date=Date.from(instant);
		
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		endTime = myFormat.format(calendar.getTime());
		return endTime;
	}
	
	
public List<Data> getDailyFowRateData(RequestData data) throws ParseException{
		
	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
	myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
	SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
	//Date startDate=myFormat.parse(data.getStart_Time());
	String startTime = data.getStart_Time();
	//String startTime = "2018-03-01";
	//String startTime=myFormat2.format(startDate);
	
	log.debug("Start time:"+startTime);
	
	
	String endTime = data.getEnd_Time();
	
	
	endTime = getNextDay( endTime);
	//Date endDate=myFormat.parse(endTime2);
	//String endTime = myFormat2.format(endDate);
		
		String groupVal = getGroupVal(data);
		
		
		int deviceId=data.getHouse_ID();
		//int deviceId=123;
		//endTime = getNextDay( endTime);
				
		
		String query ="select mean(hourlyval) from (select sum(value) as hourlyval from flowvalues where meter_id='"+deviceId+"' and time >='"+startTime+"' and time <'"+endTime+"' group by time(1h) TZ('"+influxProperties.getDatatimezone()+"')"+")  where time >='"+startTime+"' and time < '"+endTime+"' group by time("+groupVal+") fill(0)";
		log.debug("Query is:"+query);
		
		
		List<Data> retlist = getDailyMetrics(deviceId, query,false);
	//	List<Data> retlist =getDailyMetricsFlowRate(deviceId, query,false);
		return retlist;
	}

	private String getGroupVal(RequestData data) {
		int distanceValue=data.getSample_Distance_value();
		//int distanceValue=30;
		String disVal=String.valueOf(distanceValue);
		
		String code="d";
		String groupVal=null;
		log.debug("Sample Distance:"+data.getSample_Distance());
		log.debug("distanceValue:"+distanceValue);
		//String groupVal="1d";
		if(data.getSample_Distance().equalsIgnoreCase("Day")){
			code="d";
		
			groupVal=disVal+code;
		}
		else if(data.getSample_Distance().equalsIgnoreCase("Hour")){
			code="h";
			groupVal=disVal+code;
		}else if(data.getSample_Distance().equalsIgnoreCase("Month")){
			int monthgroupval=distanceValue*30;
			groupVal=String.valueOf(monthgroupval)+"d";
			
		}else{
			code="d";
			groupVal=disVal+code;
		}
		return groupVal;
	}

	private List<Data> getDailyMetrics(int deviceId, String query,boolean useHours) throws ParseException {
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		long endtime=System.currentTimeMillis();
		log.debug("Time After getDailyMetrics query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		String locationName= "TEST";
		
		List<Result> resultlist=queryResult.getResults();
	//	int recordSize=0;
		List<Data> retlist=new ArrayList<Data>();
	//	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//	dateFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		SimpleDateFormat dateFormatForDay=new SimpleDateFormat("yyyy-MM-dd");
		//dateFormatForDay.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		//Date date1=new SimpleDateFormat("yyyy-MM-DDTHH:mm:ssz").parse(sDate1);
		Data resultData=null;
		//Instant  instant=null;
		for(Result result:resultlist){
			List<Series> serieslist=result.getSeries();
			if(serieslist == null){
				break;
			}
			for(Series series:serieslist){
				List<List<Object>> valuelist=series.getValues();
				for(List<Object> results:valuelist){
					String endTimeReturned=(String)results.get(0);
				//	log.debug("endTimeReturned:"+endTimeReturned);
					/*log.debug("Date is:"+(endTimeReturned.split("T"))[0]);
					log.debug("Date2 is:"+(endTimeReturned.split("T"))[1]);*/
			//		instant= Instant.parse( endTimeReturned); 
					//Date date=java.util.Date.from(instant);
					//Date date=dateFormat.parse(endTimeReturned.split("T")[0]);
					
					resultData=new Data();
					resultData.setDevid(deviceId);
					if(useHours){
				//		log.debug("Original value:"+endTimeReturned+":Split value:"+endTimeReturned.split("\\+")[0]);
						/*Instant instant=Instant.parse(endTimeReturned.split("\\+")[0]);
						Date date = Date.from(instant);*/
						
						//Date date = formatter.parse(endTimeReturned);
						Date date = formatter.parse(endTimeReturned.split("\\+")[0]);
				//		log.debug("Date:"+date.getTime());
						resultData.setName(dateFormat.format(date));
					}else{
					//	log.debug("Original value:"+endTimeReturned+":Split value:"+endTimeReturned.split("\\+")[0]);
						//Instant instant=Instant.parse(endTimeReturned.split("\\+")[0]);
						Date date = formatter.parse(endTimeReturned.split("\\+")[0]);
						//Date date = Date.from(instant);
						
						//Date date = formatter.parse(endTimeReturned);
						//log.debug("Date:"+date.getTime());
						resultData.setName(dateFormatForDay.format(date));	
					}
						
					resultData.setValue(Math.round(((Double)results.get(1)).doubleValue()*100D)/100D);
					resultData.setSensor_locationname(locationName);
					retlist.add(resultData);
				}
				
				
			}
			
		}
		influxDB.close();
		return retlist;
	}
	
	
	private List<Data> getDailyMetricsFlowRate(int deviceId, String query,boolean useHours) {
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		long endtime=System.currentTimeMillis();
		log.debug("Time After getDailyMetrics query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
		String locationName= "TEST";
		
		List<Result> resultlist=queryResult.getResults();
	//	int recordSize=0;
		List<Data> retlist=new ArrayList<Data>();
	//	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		//Date date1=new SimpleDateFormat("yyyy-MM-DDTHH:mm:ssz").parse(sDate1);
		Data resultData=null;
		//Instant  instant=null;
		for(Result result:resultlist){
			List<Series> serieslist=result.getSeries();
			if(serieslist == null){
				break;
			}
			for(Series series:serieslist){
				List<List<Object>> valuelist=series.getValues();
				for(List<Object> results:valuelist){
					String endTimeReturned=(String)results.get(0);
					/*log.debug("Date is:"+(endTimeReturned.split("T"))[0]);
					log.debug("Date2 is:"+(endTimeReturned.split("T"))[1]);*/
			//		instant= Instant.parse( endTimeReturned); 
					//Date date=java.util.Date.from(instant);
					//Date date=dateFormat.parse(endTimeReturned.split("T")[0]);
					
					resultData=new Data();
					resultData.setDevid(deviceId);
					if(useHours){
						Instant instant=Instant.parse(endTimeReturned);
						Date date = Date.from(instant);
						resultData.setName(dateFormat.format(date));
					}else{
						//resultData.setName(endTimeReturned.split("T")[0]);
						Instant instant=Instant.parse(endTimeReturned);
						Date date = Date.from(instant);
						Calendar cal=Calendar.getInstance();
						cal.setTime(date);
						resultData.setName(cal.get(Calendar.DAY_OF_MONTH));
						resultData.setDate((String)endTimeReturned.split("T")[0]);
					}
						
					resultData.setValue(Math.round(((Double)results.get(1)).doubleValue()*100D)/100D);
					resultData.setSensor_locationname(locationName);
					retlist.add(resultData);
				}
				
				
			}
			
		}
		influxDB.close();
		return retlist;
	}
	
  public List<Data> geBatterytData(RequestData data) throws ParseException{
		
	  log.debug("Entering ConnectionStatisticsService.geBatterytData");
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date startDate=myFormat.parse(data.getStart_Time());
		//String startTime = data.getStart_Time();
		//String startTime = "2018-03-01";
		String startTime=myFormat2.format(startDate);
		
		log.debug("Start time:"+startTime);
		
		
		String endTime2 = data.getEnd_Time();
		
		
		endTime2 = getNextDay( endTime2);
		Date endDate=myFormat.parse(endTime2);
		String endTime = myFormat2.format(endDate);
		
		endTime = getNextDay( endTime);
		String groupVal = getGroupVal(data);
		
		
		int deviceId=data.getHouse_ID();
		//int deviceId=123;
		String query = "select last(value)  from batterylevelvalues where time >='"+startTime+"' and time<'"+endTime+"' and meter_id='"+deviceId+"' group by time("+groupVal+") TZ('"+influxProperties.getDatatimezone()+"')";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		log.debug("Query is:"+query);
		
		
		
		
		long jsonstarttime=System.currentTimeMillis();
		List<Data> retlist =null;
		String locationName= "TEST";
	//	retlist=getBatteryDataUsingNativeHttp(deviceId, query, jsonstarttime, locationName);
		
		
		
		
		 retlist = getBatteryResultUsingInfluxAPI(deviceId, query, locationName);
		log.debug("Exiting ConnectionStatisticsService.geBatterytData");
		return retlist;
	}

private List<Data> getBatteryDataUsingNativeHttp(int deviceId, String query, long jsonstarttime, String locationName) {
	List<Data> retlist = null;
	try {
		JSONObject responsejson=influxdbutils.executeQuery(query);
		long jsonendtime=System.currentTimeMillis();
		
		log.debug("JSON response is:"+responsejson.toString());
		log.debug("Timetake to execute as JSON is:"+(jsonendtime-jsonstarttime));
		
		retlist = querycallback.proccessBatteryUsingHTTPCall(deviceId, locationName, responsejson);
		
		
		
	} catch (IOException | JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return retlist;
}


private List<Data> getBatteryResultUsingInfluxAPI(int deviceId, String query, String locationName) throws ParseException {
	//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
	InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
	long startStarttime=System.currentTimeMillis();
	log.debug("Time before getBattery query execution:"+startStarttime);
	QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
	long endtime=System.currentTimeMillis();
	log.debug("Time After getBattery query execution:"+endtime);
	log.debug("Time Taken for query execution:"+(endtime-startStarttime));
	List<Result> resultlist=queryResult.getResults();
//	int recordSize=0;
	List<Data> retlist=new ArrayList<Data>();
	querycallback.processBatteryAPIResult(deviceId, locationName, resultlist, retlist);
	influxDB.close();
	return retlist;
}

  
  
  /**
   * 
   * @param data
   * @return
   * @throws ParseException
   */
  public List<Data> geInstanceTelemetrytData(TelemetryRequestData data) throws ParseException{
		
	  log.debug("Entering ConnectionStatisticsService.geInstanceTelemetrytData");
		
			
			int distanceValue=data.getSampleDistanceValue();
			//int distanceValue=30;
			String disVal=String.valueOf(distanceValue);
			
			String code="d";
			String groupVal=null;
			log.debug("Sample Distance:"+data.getSampleDistance());
			log.debug("distanceValue:"+distanceValue);
			boolean giveTimeStamp=false;
			//String groupVal="1d";
			if(data.getSampleDistance().equalsIgnoreCase("Day")){
				code="d";
				groupVal=disVal+code;
			}
			else if(data.getSampleDistance().equalsIgnoreCase("Hour")){
				code="h";
				groupVal=disVal+code;
				giveTimeStamp=true;
			}else if(data.getSampleDistance().equalsIgnoreCase("Month")){
				int monthgroupval=distanceValue*30;
				groupVal=String.valueOf(monthgroupval)+"d";
				
			}else{
				code="d";
				groupVal=disVal+code;
			}
			
			
			int deviceId=data.getHouseId();
			String seriesname=getSeriesForMetrics(data.getMetrics());
			
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			SimpleDateFormat myFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			myFormat3.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			
			//Date startDate=myFormat.parse(data.getStartTime());
			String startTime = data.getStartTime();
			//String startTime = "2018-03-01";
			//String startTime=myFormat2.format(startDate);
			
			log.debug("Start time:"+startTime);
			
			
			String endTime = data.getEndTime();
			
			
			endTime = getNextDay( endTime);
			/*Date endDate=myFormat.parse(endTime2);
			String endTime = myFormat2.format(endDate);*/
			//int deviceId=123;
			String query = "select first(value),last(value)  from "+ seriesname+" where time >='"+startTime+"' and time< '"+endTime+"' and meter_id='"+deviceId+"' group by time("+groupVal+") TZ('"+influxProperties.getDatatimezone()+"')";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
			if(data.getDefaultValueForMissingData()!=null){
				query = query+"fill("+data.getDefaultValueForMissingData()+")";
			}
			log.debug("Query is:"+query);
			
			
			//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
			InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
			long startStarttime=System.currentTimeMillis();
			QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
			long endtime=System.currentTimeMillis();
			log.debug("Time After getBattery query execution:"+endtime);
			log.debug("Time Taken for query execution:"+(endtime-startStarttime));
			String locationName= "TEST";
			
			List<Result> resultlist=queryResult.getResults();

			List<Data> retlist=new ArrayList<Data>();
			Data resultData=null;
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//	dateFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			SimpleDateFormat dateFormatForDay=new SimpleDateFormat("yyyy-MM-dd");
			//dateFormatForDay.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
				
				
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
		//	Instant  instant=null;
			for(Result result:resultlist){
				List<Series> serieslist=result.getSeries();
				if(serieslist == null){
					break;
				}
				for(Series series:serieslist){
					List<List<Object>> valuelist=series.getValues();
					int index=0;
					int size= valuelist.size();
					for(List<Object> results:valuelist){
						String endTimeReturned=(String)results.get(0);
						
						if(results.get(1)!=null){
							
					
						resultData=new Data();
						resultData.setDevid(deviceId);
						if(giveTimeStamp){
							/*Instant instant=Instant.parse(endTimeReturned);
							Date date=Date.from(instant);*/
							
							Date date = formatter.parse(endTimeReturned.split("\\+")[0]);
							resultData.setName(dateFormat.format(date));
						}else{
							/*Instant instant=Instant.parse(endTimeReturned);
							Date date=Date.from(instant);*/
							Date date = formatter.parse(endTimeReturned.split("\\+")[0]);
							resultData.setName(dateFormatForDay.format(date));	
						}
							
						if(index == size-1){
							resultData.setValue(Math.round(((Double)results.get(2)).doubleValue()*100D)/100D);
						}else{
							resultData.setValue(Math.round(((Double)results.get(1)).doubleValue()*100D)/100D);	
						}
						
						resultData.setSensor_locationname(locationName);
						retlist.add(resultData);
						index++;
						}
					}
					
					
				}
				
			}
			influxDB.close();
			log.debug("Entering ConnectionStatisticsService.geInstanceTelemetrytData");
			return retlist;
		}
	
	/**
	 * 
	 * @param telemetry
	 * @throws ParseException 
	 */
	@Async
	public void insertData(Telemetry telemetry) throws ParseException{
		
		log.info("Entering ConnectionStatisticsService.insertData");
		log.debug(" TimeZone is:"+influxProperties.getDatatimezone());
		log.debug(" dbname is:"+influxProperties.getDbname());
		if(telemetry.getDate()==null){
			log.info("Date is null.Setting defaule date.");
			Calendar date=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			telemetry.setDate(date.getTime());
		}else{ 
			/*ASSUMPTION THAT SENDER WILL SEND WITH CORRECT time zone. Influx by default converts that to UTC*/
			/*//Instant
			log.info("Time at starting  is:"+telemetry.getDate().getTime());
			Calendar date=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			
			//Instant instant=telemetry.getDate().toInstant();
			//instant.
			//Date inputDate=telemetry.getDate();
			//telemetry.getDate().
			Calendar inputCal=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			log.debug("Inout cal time is:"+inputCal.getTimeInMillis()+":hour:"+inputCal.get(Calendar.HOUR_OF_DAY));
			//inputCal.setTime(telemetry.getDate());
			log.debug("Inout cal time2 is:"+inputCal.getTimeInMillis()+":hour:"+inputCal.get(Calendar.HOUR_OF_DAY));
			date.set(Calendar.YEAR,inputCal.get(Calendar.YEAR));
			date.set(Calendar.MONTH,inputCal.get(Calendar.MONTH));
			date.set(Calendar.DAY_OF_MONTH, inputCal.get(Calendar.DAY_OF_MONTH));
			date.set(Calendar.HOUR_OF_DAY,inputCal.get(Calendar.HOUR_OF_DAY));
			date.set(Calendar.MINUTE,inputCal.get(Calendar.MINUTE));
			date.set(Calendar.SECOND,inputCal.get(Calendar.SECOND));
			date.set(Calendar.MILLISECOND,inputCal.get(Calendar.MILLISECOND));
			telemetry.setDate(date.getTime());
			log.info("Time to set is:"+telemetry.getDate().getTime());*/
		}
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
		InfluxDB influxDB =InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		
		influxDB.setDatabase(influxProperties.getDbname());
		influxDB.enableBatch(BatchOptions.DEFAULTS);
		String rpName = "aRetentionPolicy2";
	//	influxDB.createRetentionPolicy(rpName, influxProperties.getDbname(), "365d", "30m", 2, true);
		influxDB.setRetentionPolicy("aRetentionPolicy2");
		
		BatchPoints batchPoints = BatchPoints
				.database(influxProperties.getDbname())
//				.tag("async", "true")
				.retentionPolicy(rpName)
				.consistency(ConsistencyLevel.ALL)
				.build();
		
		
		
		if(telemetry.getFlow()!=null){
			insertFlow(telemetry,influxDB,batchPoints);
			
			if(telemetry.getReading() == null){
				SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
				log.debug("Date format is:"+myFormat.format(telemetry.getDate()));
				double meterReading=getLastMeterReading(myFormat.format(telemetry.getDate()), telemetry.getMeter_id());
				log.debug("last flow value:"+meterReading);
				double newmeterreading= meterReading+telemetry.getFlow();
				telemetry.setReading(newmeterreading);
			}
		}else{
			log.info("No flow data. Not inserting");
		}
		
		
		if(telemetry.getBattery()!=null){
			insertBattery(telemetry,batchPoints);
		}else{
			log.info("No battery data. Not inserting");
		}
		
		if(telemetry.getFlowrate()!=null){
			insertFlowrate(telemetry,batchPoints);
		}else{
			log.info("No flowrate data. Not inserting");
		}
		
		if(telemetry.getReading()!=null){
			insertMeterReading(telemetry,batchPoints);
		}else{
			log.info("No meterreading data. Not inserting");
		}
		
		//influxDB.flush();
		influxDB.write(batchPoints);

		influxDB.close();
		
		log.info("Exiting ConnectionStatisticsService.insertData");
		
	}
	
	private void insertFlow(Telemetry telemetry,InfluxDB influxDB ,BatchPoints  batchPoints){
		
		log.info("Entering ConnectionStatisticsService.insertFlow");
		
		/*
		influxDB.write(Point.measurement("flow")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getFlow())
				.build());
*/
		Map<String,String> tagMap=new HashMap<String,String>();
		tagMap.put("meter_id", Integer.toString(telemetry.getMeter_id()));
		Point point1 = Point.measurement("flowvalues")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.tag(tagMap)
				//.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getFlow())
				.build();

		batchPoints.point(point1);
		
	
		log.debug("Inserted flow into db");
		log.info("Exiting ConnectionStatisticsService.insertFlow");
		
	}
	private void insertBattery(Telemetry telemetry,BatchPoints  batchPoints){
		
		log.info("Entering ConnectionStatisticsService.insertBattery");
		
		/*influxDB.write(Point.measurement("batterylevel")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getBattery())
				.build());
		*/
		Map<String,String> tagMap=new HashMap<String,String>();
		tagMap.put("meter_id", Integer.toString(telemetry.getMeter_id()));
		Point point1 = Point.measurement("batterylevelvalues")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				//.addField("meter_id", telemetry.getMeter_id())
				.tag(tagMap)
				.addField("value", telemetry.getBattery())
				.build();

		batchPoints.point(point1);
	
		log.debug("Inserted battery into db");
		
		log.info("Exiting ConnectionStatisticsService.insertBattery");
		
	}
	
	
	
	
	private void insertFlowrate(Telemetry telemetry,BatchPoints  batchPoints){
		
		log.info("Entering ConnectionStatisticsService.insertFlowrate");
	
		/*influxDB.write(Point.measurement("flowrate")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getFlowrate())
				.build());*/
	
		Map<String,String> tagMap=new HashMap<String,String>();
		tagMap.put("meter_id",Integer.toString( telemetry.getMeter_id()));
		Point point1 = Point.measurement("flowratevalues")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.tag(tagMap)
				//.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getFlowrate())
				.build();
		batchPoints.point(point1);
		log.debug("Inserted Flowrate into db");
		
		log.info("Exiting ConnectionStatisticsService.insertFlowrate");
		
	}
	
 private void insertMeterReading(Telemetry telemetry,BatchPoints  batchPoints){
		
		log.info("Entering ConnectionStatisticsService.insertMeterReading");
	
		/*influxDB.write(Point.measurement("flowrate")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getFlowrate())
				.build());*/
	
		Map<String,String> tagMap=new HashMap<String,String>();
		tagMap.put("meter_id",Integer.toString( telemetry.getMeter_id()));
		Point point1 = Point.measurement("meterreadingvalues")
				.time(telemetry.getDate().getTime(), TimeUnit.MILLISECONDS)
				.tag(tagMap)
				//.addField("meter_id", telemetry.getMeter_id())
				.addField("value", telemetry.getReading())
				.build();
		batchPoints.point(point1);
		log.debug("Inserted meterreading into db");
		
		log.info("Exiting ConnectionStatisticsService.insertMeterReading");
		
	}
 
 /**
  * This methods gets data for specific meter for the day
  * @param meterId
  * @return
  * @throws ParseException
  */
 
    public ConnectionKpiData getAllMetricsForConnectionForDay(int meterId) throws ParseException{
    	
    	
    	log.info("Entering ConnectionStatisticsService.getAllMetricsForConnectionForDay");
    	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
    	Calendar dayStart=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
    	dayStart.set(Calendar.HOUR_OF_DAY, 0);
    	dayStart.set(Calendar.MINUTE, 0);
    	dayStart.set(Calendar.SECOND, 0);
    	
    	
    	Calendar dayend=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
    	dayend.set(Calendar.HOUR_OF_DAY, 0);
    	dayend.set(Calendar.MINUTE, 59);
    	dayend.set(Calendar.SECOND, 59);
    	dayend.add(Calendar.DATE,1);
    	
    	RequestData requestData=new RequestData();
    	requestData.setHouse_ID(meterId);
    	
    	requestData.setStart_Time(myFormat.format(dayStart.getTime()));
    	requestData.setEnd_Time(myFormat.format(dayend.getTime()));
    	requestData.setSample_Distance_value(1);
    	requestData.setSample_Distance("Day");
    	List<Data> batteryData=geBatterytData(requestData);
    	
    	Double batterylevel = new Double(2000);
    	if(batteryData != null && batteryData.size()>0){
    		batterylevel=batteryData.get(0).getValue();	
    	}
    	List<Data> consumptionData=getData(requestData);
    	Double consumption = new Double(0);
    	if(consumptionData != null && consumptionData.size()>0){
    		consumption=(consumptionData.get(0).getValue());	
    	}
    	
    	int status=getCurrentDeviceStatus(meterId);
    	
    	Kpi consumptionkpi= new Kpi("Consumption",consumption);
    	Kpi batterykpi= new Kpi("Battery",batterylevel);
    	String deviceStatus="IN ACTIVE";
    	if(status == 0){
    		deviceStatus="NOT WORKING";
    	}else if(status == 1){
    		deviceStatus="WORKING";
    	}else if(status == -1){
    		deviceStatus="IN ACTIVE";
    	}
    	Kpi statuskpi=new Kpi("Status",deviceStatus);
    	
    	List<Kpi> kpiList=new ArrayList<Kpi>();
    	kpiList.add(consumptionkpi);
    	kpiList.add(batterykpi);
    	kpiList.add(statuskpi);
    	
    	ConnectionKpiData data=new ConnectionKpiData(meterId, kpiList);
    	
    	
    	log.info("Exiting ConnectionStatisticsService.getAllMetricsForConnectionForDay");
    	return data;
    	
    }
    
    public int getCurrentDeviceStatus(int meterId){
    	log.info("Entering ConnectionStatisticsService.getCurrentDeviceStatus");
    	
    		
    	String query = "select last(value)  from devicestatus where  meter_id='"+meterId+"'";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		log.debug("Query is:"+query);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32770", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		long startStarttime=System.currentTimeMillis();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
    	log.info("Entering ConnectionStatisticsService.getCurrentDeviceStatus");
    	long endtime=System.currentTimeMillis();
		log.debug("Time After getBattery query execution:"+endtime);
		log.debug("Time Taken for query execution:"+(endtime-startStarttime));
    	List<Result> resultList=queryResult.getResults();
    	
    	int returnval=-1;
    	
    	if(resultList != null && resultList.size()>0){
    		Result result=resultList.get(0);
    		
    		List<Series> serieslist=result.getSeries();
    		if(serieslist != null && serieslist.size()>0){
    			Series series=serieslist.get(0);
    			
    			List<List<Object>> resultrow=series.getValues();
    			if(resultrow != null && resultrow.size()>0){
    				List<Object> row=resultrow.get(0);
    				returnval=((Double)row.get(1)).intValue();
    			}
    					
    		}
    	}
    	influxDB.close();
    	return returnval;
    	
    	
    	
    }
    
    /**
     * 
     * @param metrics
     * @return
     */
    private String getSeriesForMetrics(String metrics){
    	//TODO CHange this to take from DB or file
    	String returnVal=null;
    	
    	if("readings".equalsIgnoreCase(metrics)){
    		returnVal="meterreadingvalues";
    	}else if("meteron".equalsIgnoreCase(metrics)){
    		returnVal="supplyondata";
    	}else if ("meteroff".equalsIgnoreCase(metrics)){
    		returnVal="supplyoffdata";
    	}else if("consumerleakageontime".equalsIgnoreCase(metrics)){
    		returnVal="consumerleakageontime";
    	}else if("consumerleakageofftime".equalsIgnoreCase(metrics)){
    		returnVal="consumerleakageofftime";
    	}
    	else if("roomambience".equalsIgnoreCase(metrics)){
    		returnVal="roomweather";
    	}
    	else if("batteryhealth".equalsIgnoreCase(metrics)){
    		returnVal="battery_health";
    	}else if ("transformer".equalsIgnoreCase(metrics)){
    		returnVal="trans_telemetry_data";
    	}
    	return returnVal;
    }

    private double getLastMeterReading(String startTime2,int meterId) throws ParseException{
    	
    	
    	double baseReadingValue =0;
    	
    	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date startDate=myFormat.parse(startTime2);
		String startTime=myFormat2.format(startDate);
    	
    	String query = "select last(value)  from meterreadingvalues where time <='"+startTime2+"' and meter_id='"+meterId+"' TZ('"+influxProperties.getDatatimezone()+"')";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		log.info("Query is:"+query);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		
		List<Result> results=queryResult.getResults();
		if(results != null && results.size()>0){
			Result result  = results.get(0);
			
			List<Series> serieslist=result.getSeries();
			if(serieslist !=null && serieslist.size()>0){
				Series series=serieslist.get(0);
				List<List<Object>> objects=series.getValues();
				List<Object> resultvals=objects.get(0);
				Double double1=(Double)resultvals.get(1);
				baseReadingValue=double1.doubleValue();
			}
				
			
		}
		return baseReadingValue;
    	
    }
    
    public Double getAverageMonthlyForOneYear(int meterId) throws ParseException{
    	
    	log.info("Entering ConnectionStatisticsService.getAverageMonthlyForOneYear");
    	
    	Calendar endTimeCal=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
    	endTimeCal.set(Calendar.DAY_OF_MONTH, 1);
    	//endTimeCal.add(Calendar.DATE, -1);
    	
    	Calendar startTimeCal=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
    	startTimeCal.set(Calendar.DAY_OF_MONTH, 1);
    	startTimeCal.add(Calendar.YEAR, -1);
    	
    	Double resultValue=0.0;
    	
    	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
    	
    	myFormat.setTimeZone(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
		SimpleDateFormat myFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		myFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		Date startDate=startTimeCal.getTime();
    	
    	
    	String startTime=myFormat2.format(startDate);
    	//Date endDate2=endTimeCal.getTime();
    	
    	String endTime2=myFormat.format(endTimeCal.getTime());
    	
    	endTime2 = getNextDay( endTime2);
    	Date endDate=myFormat.parse(endTime2);
		String endTime = myFormat2.format(endDate);
    	String query = "select mean(monthlyconsumption) from (select sum(value) as monthlyconsumption from flowvalues where time >='"+startTime+"' and time<'"+endTime+"' and meter_id='"+meterId+"' group by time(30d))";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		log.info("Query is:"+query);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		//String influxProperties.getDbname() = influxProperties.getDbname();
		QueryResult queryResult = influxDB.query(new Query(query, influxProperties.getDbname()));
		
		List<Result> results=queryResult.getResults();
		if(results != null && results.size()>0){
			Result result  = results.get(0);
			
			List<Series> serieslist=result.getSeries();
			if(serieslist !=null && serieslist.size()>0){
				Series series=serieslist.get(0);
				List<List<Object>> objects=series.getValues();
				List<Object> resultvals=objects.get(0);
				resultValue=(Double)resultvals.get(1);
				if(resultValue!=null){
					resultValue= Math.round(resultValue*100D)/100D;
				}
				
			}
				
			
		}
    	log.info("Exiting ConnectionStatisticsService.getAverageMonthlyForOneYear");
    	return resultValue;
    	
    }
    
    public void insertTimeSeriesData(SeriesPointData pointData){
    	
    	
    	InfluxDB influxDB =InfluxDBFactory.connect(influxProperties.getUrl(),influxProperties.getUsername(),influxProperties.getPassword());
		
    	log.info("Entering ConnectionStatisticsService.insertTimeSeriesData");

		influxDB.setDatabase(influxProperties.getDbname());
		influxDB.enableBatch(BatchOptions.DEFAULTS);
		String rpName = influxProperties.getRetentionpolicy();
	//	influxDB.createRetentionPolicy(rpName, influxProperties.getDbname(), "365d", "30m", 2, true);
		influxDB.setRetentionPolicy("aRetentionPolicy");
		
		BatchPoints batchPoints = BatchPoints
				.database(influxProperties.getDbname())
//				.tag("async", "true")
				.retentionPolicy(rpName)
				.consistency(ConsistencyLevel.ALL)
				.build();
		
		String name=pointData.getName();
		String seriesName=getSeriesForMetrics(name);
		
		Map<String,String> tagMap=new HashMap<String,String>();
		
		List<KeyValue> tags=pointData.getTags();
		tags.forEach(tag -> {
			tagMap.put(tag.getKey(),tag.getValue().toString());
		});
		
		Map<String,Object> filedMap=new HashMap<String,Object>();
		List<KeyValue> fieldlist=pointData.getValues();
		
		fieldlist.forEach(tag -> {
			filedMap.put(tag.getKey(),tag.getValue());
		});
		
		Date timeStamp=pointData.getTimestamp();
		
		if(timeStamp ==null){
			Calendar cal=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
			timeStamp=cal.getTime();
		}else{
			//NOT DOING ANTHING. kEEPING THE DEFAULT TIME SENT AS IT IS.
			
				
				/*Calendar date=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
				//Date inputDate=telemetry.getDate();
				Calendar inputCal=Calendar.getInstance(TimeZone.getTimeZone(influxProperties.getDatatimezone()));
				inputCal.setTime(pointData.getTimestamp());
				date.set(Calendar.YEAR,inputCal.get(Calendar.YEAR));
				date.set(Calendar.MONTH,inputCal.get(Calendar.MONTH));
				date.set(Calendar.DAY_OF_MONTH, inputCal.get(Calendar.DAY_OF_MONTH));
				date.set(Calendar.HOUR,inputCal.get(Calendar.HOUR_OF_DAY));
				date.set(Calendar.MINUTE,inputCal.get(Calendar.MINUTE));
				date.set(Calendar.SECOND,inputCal.get(Calendar.SECOND));
				date.set(Calendar.MILLISECOND,inputCal.get(Calendar.MILLISECOND));
				pointData.setTimestamp(date.getTime());*/
				
			}
		
		
		
		
		Point point1 = Point.measurement(seriesName)
				.time(timeStamp.getTime(), TimeUnit.MILLISECONDS)
				.tag(tagMap)
				.fields(filedMap)
				.build();
				

		
		batchPoints.point(point1);
		
		influxDB.write(batchPoints);

		influxDB.close();
		
		log.info("Exiting ConnectionStatisticsService.insertTimeSeriesData");
		

		
    }

}
