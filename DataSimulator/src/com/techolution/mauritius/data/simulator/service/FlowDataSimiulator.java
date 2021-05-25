package com.techolution.mauritius.data.simulator.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.techolution.mauritus.data.Telemetry;

public class FlowDataSimiulator implements IStubData {

	
	private static String INFLUX_CONNECTION_STRING="http://10.128.0.12:8086";
	private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root"; 
	Logger log=Logger.getLogger(FlowDataSimiulator.class.getName());
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
		
		double baseReadingValue = 20000;
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
		
		String query = "select last(value)  from meterreadingvalues where time <'"+startTime+"' and meter_id='"+meterId+"'";// now() - 10d and meter_id = '124' group by time(1d) fill(0)
		System.out.println("Query is:"+query);
		
		
		//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:32768", "root", "root");
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_CONNECTION_STRING, INFLUX_USERNAME, INFLUX_PWD);
		String dbName = "mauritius_smartwater_uat";
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		List<Result> results=queryResult.getResults();
		if(results != null && results.size()>0){
			Result result  = results.get(0);
			
			List<Series> serieslist=result.getSeries();
			if(serieslist !=null && serieslist.size()>0){
				Series series=serieslist.get(0);
				List<List<Object>> objects=series.getValues();
				List<Object> resultvals=objects.get(0);
				Double double1=(Double)resultvals.get(1);
				baseReadingValue=double1.longValue();
			}
				
			
		}
		boolean loop=true;
		while(startDate.before( endDate)){
			//System.out.println("Start date millis:"+startDate.getTime());
			
			try {
				Telemetry telemetry=new Telemetry();
				telemetry.setDate(startDate);
				double flow=ThreadLocalRandom.current().nextDouble(1.00, 2.00); 
				telemetry.setFlow(flow);
				baseReadingValue=baseReadingValue+flow;
				telemetry.setReading(baseReadingValue);
				telemetry.setMeter_id(meterId);
				sendRequest(telemetry,startDate);
				
				if(sleepTime > 100){
					Thread.sleep(sleepTime);
				}
				Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("Indian/Mauritius"));
				calendar.setTime(startDate);
				calendar.add(Calendar.MILLISECOND,incrementtime);
				startDate=calendar.getTime();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}
	
	private void sendRequest(Telemetry telemetry,Date date){
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dateVal=myFormat.format(date);
		log.info("Dateval to inout is:"+dateVal);
		JSONObject json = new JSONObject();
		json.put("flow", telemetry.getFlow());    
		json.put("reading", telemetry.getReading());
		json.put("date", dateVal);
		//System.out.println("String value is:"+json.toString());

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
		    HttpPost request = new HttpPost("http://localhost:8085/insert/telemetry/data/"+telemetry.getMeter_id());
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    httpClient.execute(request);
		// handle response here...
		} catch (Exception ex) {
		    // handle exception here
		} finally {
		    try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
